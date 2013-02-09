package org.zeromq.api;

/**
 * Send and Receive flags
 */
public enum MessageFlag {
    /**
     * Specifies no flags.
     */
    NONE {
        @Override
        public int getFlag() {
            return 0;
        }
    },
    /**
     * Specifies that the operation should be performed in non-blocking mode.
     */
    DONT_WAIT {
        @Override
        public int getFlag() {
            return 1;
        }
    },
    /**
     * Specifies that the message being sent is a multi-part message, and that further message parts are to follow.
     */
    SEND_MORE {
        @Override
        public int getFlag() {
            return 2;
        }
    };

    /**
     * Send and receive flag constant
     * 
     * @return flag
     */
    public abstract int getFlag();
}
