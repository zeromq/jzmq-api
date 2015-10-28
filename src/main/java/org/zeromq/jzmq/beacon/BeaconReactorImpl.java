package org.zeromq.jzmq.beacon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.BeaconListener;
import org.zeromq.api.BeaconReactor;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
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

    private final ManagedContext context;
    private final Reactor reactor;
    private final UdpSocket socket;
    private final UdpBeacon beacon;
    private BeaconListener listener;
    private long broadcastInterval = DEFAULT_BROADCAST_INTERVAL;
    private boolean ignoreLocalAddress = false;

    public BeaconReactorImpl(ManagedContext context, int broadcastPort, UdpBeacon beacon) throws IOException {
        this.context = context;
        this.beacon = beacon;
        this.socket = new UdpSocket(broadcastPort);
        this.reactor = context.buildReactor()
            .build();
    }

    @Override
    public void start() {
        assert (listener != null);
        reactor.addTimer(broadcastInterval, -1, SEND_BEACON);
        reactor.addPollable(context.newPollable(socket.getChannel(), PollerType.POLL_IN), RECEIVE_BEACON);
        reactor.start();
    }

    @Override
    public void stop() {
        reactor.stop();
    }

    public void setListener(BeaconListener listener) {
        this.listener = listener;
    }

    public void setBroadcastInterval(long broadcastInterval) {
        this.broadcastInterval = broadcastInterval;
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

                // Attempt to ignore local addresses
                // 
                // NOTE: This does not seem to work on certain network configurations,
                // as the loopback address is returned from InetAddress.getLocalHost()
                // but the sender is a resolved address
                InetAddress sender = ((InetSocketAddress) socket.getSender()).getAddress();
                if (ignoreLocalAddress &&
                        (socket.getAddress().getHostAddress().equals(sender.getHostAddress())
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

                // Last ditch effort to ignore our own packets
                // Only ignores this reactor instance, not others broadcasting on this server
                if (ignoreLocalAddress
                        && beacon.getIdentity().equals(message.getIdentity())) {
                    return;
                }

                listener.onBeacon(sender, message);
            } catch (IOException ex) {
                log.error("Unable to receive UDP beacon:", ex);
            }
        }
    };
}
