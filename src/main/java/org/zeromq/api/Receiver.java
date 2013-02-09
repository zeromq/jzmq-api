package org.zeromq.api;

/**
 * 
 */
public interface Receiver {
    /**
     * 
     * @return
     */
    public byte[] receive();

    /**
     * 
     * @param flag message receive flag
     * @return bytes
     */
    public byte[] receive(MessageFlag flag);
}
