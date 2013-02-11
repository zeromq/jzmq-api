package org.zeromq.api;

/**
 * 
 */
public interface Receiver {
    /**
     * 
     * @return
     */
     byte[] receive();

    /**
     * 
     * @param flag message receive flag
     * @return bytes
     */
    byte[] receive(MessageFlag flag);

    /**
     * This receiver has more of a multi-part message waiting for receipt.
     */
    boolean hasMoreToReceive();
}
