package org.zeromq.api;

/**
 * Enumeration of poller types.
 */
public enum PollerType {
    POLL_IN(1), POLL_OUT(2), POLL_ERROR(4);
    
    private final int type;

    PollerType(int type) {
        this.type = type;
    }

    /**
     * The integer representation of this poller type.
     * 
     * @return The integer representation of this poller type
     */
    public int getType() {
        return type;
    }
}
