package org.zeromq.api;

import org.zeromq.ZMQ;

/**
 * Values for socket types
 */
public enum SocketType {
    PAIR(ZMQ.PAIR), PUB(ZMQ.PUB), SUB(ZMQ.SUB), REQ(ZMQ.REQ), REP(ZMQ.REP), DEALER(ZMQ.DEALER), ROUTER(ZMQ.ROUTER), PUSH(
            ZMQ.PUSH), PULL(ZMQ.PULL), XPUB(ZMQ.XPUB), XSUB(ZMQ.XSUB);

    private final int type;

    SocketType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
