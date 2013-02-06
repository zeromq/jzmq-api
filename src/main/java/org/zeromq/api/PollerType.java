package org.zeromq.api;

public enum PollerType {
    POLLIN(1), POLLOUT(2), POLLERR(4);
    private final int type;

    PollerType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
