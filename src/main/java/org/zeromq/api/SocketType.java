package org.zeromq.api;

/**
 * Values for Socket types
 */
public enum SocketType {
    /**
     * A socket of type ZMQ_PAIR can only be connected to a single peer at any one time. No message routing or filtering
     * is performed on messages sent over a ZMQ_PAIR socket.
     */
    PAIR {
        @Override
        public int getType() {
            return 0;
        }
    },
    /**
     * A socket of type ZMQ_PUB is used by a publisher to distribute data. Messages sent are distributed in a fan out
     * fashion to all connected peers.
     */
    PUB {
        @Override
        public int getType() {
            return 1;
        }
    },
    /**
     * A socket of type ZMQ_SUB is used by a subscriber to subscribe to data distributed by a publisher. Initially a
     * ZMQ_SUB socket is not subscribed to any messages.
     */
    SUB {
        @Override
        public int getType() {
            return 2;
        }
    },
    /**
     * A socket of type ZMQ_REP is used by a service to receive requests from and send replies to a client. This socket
     * type allows only an alternating sequence of receive(request) and subsequent send(reply) calls.
     */
    REQ {
        @Override
        public int getType() {
            return 3;
        }
    },
    /**
     * A socket of type ZMQ_REQ is used by a client to send requests to and receive replies from a service. This socket
     * type allows only an alternating sequence of send(request) and subsequent receive(reply) calls.
     */
    REP {
        @Override
        public int getType() {
            return 4;
        }
    },
    /**
     * A socket of type ZMQ_DEALER is an advanced pattern used for extending request/reply sockets. Each message sent is
     * round-robined among all connected peers, and each message received is fair-queued from all connected peers.
     */
    DEALER {
        @Override
        public int getType() {
            return 5;
        }
    },
    /**
     * A socket of type ZMQ_ROUTER is an advanced socket type used for extending request/reply sockets. When receiving
     * messages a ZMQ_ROUTER socket shall prepend a message part containing the identity of the originating peer to the
     * message before passing it to the application.
     */
    ROUTER {
        @Override
        public int getType() {
            return 6;
        }
    },
    /**
     * A socket of type ZMQ_PULL is used by a pipeline node to receive messages from upstream pipeline nodes. Messages
     * are fair-queued from among all connected upstream nodes.
     */
    PULL {
        @Override
        public int getType() {
            return 7;
        }
    },
    /**
     * A socket of type ZMQ_PUSH is used by a pipeline node to send messages to downstream pipeline nodes. Messages are
     * round-robined to all connected downstream nodes.
     */
    PUSH {
        @Override
        public int getType() {
            return 8;
        }
    },
    /**
     * Same as ZMQ_PUB except that you can receive subscriptions from the peers in form of incoming messages.
     */
    XPUB {
        @Override
        public int getType() {
            return 9;
        }
    },
    /**
     * Same as ZMQ_SUB except that you subscribe by sending subscription messages to the socket.
     */
    XSUB {
        @Override
        public int getType() {
            return 10;
        }
    };

    /**
     * Socket type which determines the semantics of communication over the socket.
     * 
     * @return socket type
     */
    public abstract int getType();
}
