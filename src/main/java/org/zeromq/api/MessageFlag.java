package org.zeromq.api;

/**
 * Send and Receive flags
 */
public enum MessageFlag {

    /**
     * Specifies no flags.
     */
    NONE(0),
    /**
     * Specifies that the operation should be performed in non-blocking mode.
     */
    DONT_WAIT(1),
    /**
     * Specifies that the message being sent is a multi-part message, and that further message parts are to follow.
     */
    SEND_MORE(2);

    private final int flag;

    private MessageFlag(int flag) {
        this.flag = flag;
    }

    /**
     * Send and receive flag constant
     * 
     * @return flag
     */
    public int getFlag() {
        return flag;
    }
}
