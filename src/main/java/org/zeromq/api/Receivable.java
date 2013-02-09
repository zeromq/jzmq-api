package org.zeromq.api;

/**
 * 
 */
public interface Receivable {
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
