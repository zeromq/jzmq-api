package org.zeromq.jzmq.bstar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.Backgroundable;
import org.zeromq.api.Context;
import org.zeromq.api.Message;
import org.zeromq.api.MessageFlag;
import org.zeromq.api.PollAdapter;
import org.zeromq.api.Poller;
import org.zeromq.api.Socket;

import java.util.concurrent.atomic.AtomicBoolean;

class BinaryStarClient implements Backgroundable {
    private final Logger log = LoggerFactory.getLogger(BinaryStarClient.class);

    /**
     * States we can be in at any point in time.
     */
    private enum State {
        CONNECTING,
        ACTIVE,
        FORWARDING
    }

    private BinaryStarSocketBuilder socketBuilder;
    private String url1;
    private String url2;
    private long heartbeatInterval;
    private AtomicBoolean closed = new AtomicBoolean(false);

    public BinaryStarClient(BinaryStarSocketBuilder socketBuilder, String url1, String url2, long heartbeatInterval) {
        this.socketBuilder = socketBuilder;
        this.url1 = url1;
        this.url2 = url2;
        this.heartbeatInterval = heartbeatInterval;
    }

    @Override
    public void run(Context context, Socket pipe, Object... args) {
        Socket socket = null;
        Message message = null;
        Poller poller = null;

        boolean primary = true;
        State state = State.CONNECTING;
        while (!closed.get()) {
            if (state == State.CONNECTING) {
                if (socket != null) {
                    // Old socket is confused; close it and open a new one
                    poller.disable(socket);
                    socket.close();
                    primary = !primary;
                }

                socket = createSocket(primary);
                poller = context.buildPoller()
                    .withInPollable(pipe, new PollAdapter())
                    .withInPollable(socket, new PollAdapter())
                    .build();

                if (message != null) {
                    // Send request again, on new socket
                    state = State.FORWARDING;
                } else {
                    state = State.ACTIVE;
                }
            } else if (state == State.FORWARDING) {
                // We send a request, then we work to get a reply
                socket.send(message);

                // Poll socket for a reply, with timeout
                poller.poll(heartbeatInterval * 2);

                // We use a Lazy Pirate strategy in the client. If there's no
                // reply within our timeout, we close the socket and try again.
                // In Binary Star, it's the client vote that decides which
                // server is primary; the client must therefore try to connect
                // to each server in turn:

                Message reply = socket.receiveMessage(MessageFlag.DONT_WAIT);
                if (reply != null) {
                    // We got a reply from the server
                    log.debug("Server replied OK");
                    pipe.send(reply);
                    message = null;

                    state = State.ACTIVE;
                } else {
                    log.warn("No response from server, failing over");
                    state = State.CONNECTING;
                }
            } else {
                // Wait for a message from client socket
                poller.poll();
                message = pipe.receiveMessage();

                state = State.FORWARDING;
            }
        }
    }

    private Socket createSocket(boolean primary) {
        String url = primary ? url1 : url2;
        log.info("Connecting to server at {}", url);
        return socketBuilder.connect(primary ? url1 : url2);
    }

    @Override
    public void onClose() {
        closed.set(true);
    }
}
