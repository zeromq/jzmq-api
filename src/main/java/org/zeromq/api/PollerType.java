package org.zeromq.api;

public enum PollerType {
    POLL_IN(1), POLL_OUT(2), POLL_ERROR(4);
    private final int type;

    PollerType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
