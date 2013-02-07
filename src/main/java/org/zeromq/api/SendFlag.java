package org.zeromq.api;

import org.zeromq.ZMQ;

/**
 * Send Flags 
 */
public enum SendFlag {
    NONE(0), DONTWAIT(ZMQ.DONTWAIT), SNDMORE(ZMQ.SNDMORE);

    private final int flag;

    SendFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

}
