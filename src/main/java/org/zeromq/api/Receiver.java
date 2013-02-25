package org.zeromq.api;

import java.nio.ByteBuffer;

/**
 * 
 */
public interface Receiver {
    /**
     * Receive a message part from a socket
     * 
     * @return bytes
     */
    byte[] receive();

    /**
     * Receive a message part from a socket
     * 
     * @param flag message receive flag
     * @return bytes
     */
    byte[] receive(MessageFlag flag);

    /**
     * 
     * @param buf
     * @param offset
     * @param len
     * @param flag
     * @return
     */
    int receive(byte[] buf, int offset, int len, MessageFlag flag);

    /**
     * Receive a zero copy message part from a socket
     * 
     * @param buf
     * @param len
     * @param flag
     * @return the number of bytes received
     */
    int receiveZeroCopy(ByteBuffer buf, int len, MessageFlag flag);

    /**
     * This receiver has more of a multi-part message waiting for receipt.
     */
    boolean hasMoreToReceive();

    /**
     * @return The full message (all frames) from the socket.
     */
    Message receiveMessage();

    /**
     * @return The full message (all frames) from the socket, assuming it has been through a Router socket, and has
     * routing frames associated with it.
     */
    RoutedMessage receiveRoutedMessage();
}
