package org.zeromq.api;

import org.zeromq.ZMQ;

/**
 * Values for device types
 */
public enum DeviceType {
    STREAMER(ZMQ.STREAMER), FORWARDER(ZMQ.FORWARDER), QUEUE(ZMQ.QUEUE);

    private final int type;

    DeviceType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
