package org.zeromq.jzmq.beacon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.BeaconListener;
import org.zeromq.api.BeaconReactor;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.Pollable;
import org.zeromq.api.Reactor;
import org.zeromq.api.UdpBeacon;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.UdpSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class BeaconReactorImpl implements BeaconReactor {
    private static final Logger log = LoggerFactory.getLogger(BeaconReactorImpl.class);
    private static final long DEFAULT_BROADCAST_INTERVAL = 1000L;

    private final Reactor reactor;
    private final UdpSocket socket;
    private final UdpBeacon beacon;
    private BeaconListener listener;
    private boolean ignoreLocalAddress = false;

    public BeaconReactorImpl(ManagedContext context, int broadcastPort, UdpBeacon beacon) throws IOException {
        this.beacon = beacon;
        this.socket = new UdpSocket(broadcastPort);
        this.reactor = context.buildReactor()
            .withInPollable(socket.getChannel(), RECEIVE_BEACON)
            .withTimerRepeating(DEFAULT_BROADCAST_INTERVAL, SEND_BEACON)
            .build();
    }

    @Override
    public void start() {
        assert (listener != null);
        reactor.start();
    }

    @Override
    public void stop() {
        reactor.stop();
    }

    public void setListener(BeaconListener listener) {
        this.listener = listener;
    }

    public void setIgnoreLocalAddress(boolean ignoreLocalAddress) {
        this.ignoreLocalAddress = ignoreLocalAddress;
    }

    private final LoopHandler SEND_BEACON = new LoopHandler() {
        @Override
        public void execute(Reactor reactor, Pollable pollable, Object... args) {
            try {
                socket.send(beacon.getBuffer());
            } catch (IOException ex) {
                log.error("Unable to send UDP beacon:", ex);
            }
        }
    };

    private final LoopHandler RECEIVE_BEACON = new LoopHandler() {
        private ByteBuffer buffer = ByteBuffer.allocate(Short.MAX_VALUE);

        @Override
        public void execute(Reactor reactor, Pollable pollable, Object... args) {
            try {
                int read = socket.receive(buffer);
                buffer.rewind();
                if (socket.getSender() == null
                        || read < UdpBeacon.BEACON_SIZE) {
                    return;
                }

                InetAddress sender = ((InetSocketAddress) socket.getSender()).getAddress();
                if (ignoreLocalAddress &&
                        (InetAddress.getLocalHost().getHostAddress().equals(sender.getHostAddress())
                        || sender.isAnyLocalAddress()
                        || sender.isLoopbackAddress())) {
                    return;
                }

                UdpBeacon message = new UdpBeacon(buffer);
                buffer.rewind();
                if (!beacon.getProtocol().equals(message.getProtocol())
                        || beacon.getVersion() != message.getVersion()) {
                    return;
                }

                listener.onBeacon(sender, message);
            } catch (IOException ex) {
                log.error("Unable to receive UDP beacon:", ex);
            }
        }
    };
}
