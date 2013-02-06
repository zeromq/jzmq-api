package org.zeromq.api;

import org.zeromq.ZMQ;

/**
 * Receive Flags 
 */
public enum ReceiveFlag {
    NONE(-1), NOBLOCK(ZMQ.NOBLOCK);

    private final int flag;

    ReceiveFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
