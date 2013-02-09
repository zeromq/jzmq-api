package org.zeromq.api;

/**
 * 
 */
public interface Receivable {
    /**
     * 
     * @return
     * @throws Exception
     */
    public byte[] receive() throws Exception;

    /**
     * 
     * @param flag message receive flag
     * @return bytes
     * @throws Exception
     */
    public byte[] receive(MessageFlag flag) throws Exception;
}
