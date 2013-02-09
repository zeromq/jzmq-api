package org.zeromq.api;

/**
 * 
 */
public interface Sender {
    /**
     * Insert the specified element into the ØMQ Socket queue.
     * 
     * @param buf element
     */
    public boolean send(byte[] buf);

    /**
     * Insert the specified element into the ØMQ Socket queue.
     * 
     * @param buf
     * @param offset
     * @param flag send flag
     */
    public boolean send(byte[] buf, int offset, MessageFlag flag);
}
