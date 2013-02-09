package org.zeromq.api;

/**
 * Values for Device types
 */
public enum DeviceType {
    /**
     * ZMQ_STREAMER collects tasks from a set of pushers and forwards these to a set of pullers. You will generally use
     * this to bridge networks. Messages are fair-queued from pushers and load-balanced to pullers.
     */
    STREAMER {
        @Override
        public int getType() {
            return 1;
        }
    },
    /**
     * ZMQ_FORWARDER collects messages from a set of publishers and forwards these to a set of subscribers. You will
     * generally use this to bridge networks, e.g. read on TCP unicast and forward on multicast.
     */
    FORWARDER {
        @Override
        public int getType() {
            return 2;
        }
    },
    /**
     * ZMQ_QUEUE creates a shared queue that collects requests from a set of clients, and distributes these fairly among
     * a set of services. Requests are fair-queued from frontend connections and load-balanced between backend
     * connections. Replies automatically return to the client that made the original request.
     */
    QUEUE {
        @Override
        public int getType() {
            return 3;
        }
    };
    /**
     * Device type connects a frontend socket to a backend socket.
     * 
     * @return device type
     */
    public abstract int getType();
}
