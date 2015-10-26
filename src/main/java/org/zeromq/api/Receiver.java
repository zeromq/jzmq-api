package org.zeromq.api;

import java.nio.ByteBuffer;

/**
 * Socket capable of receiving message parts.
 */
public interface Receiver {
    /**
     * Receive a message part from a socket.
     * 
     * @return The message bytes
     */
    byte[] receive();

    /**
     * Receive a message part from a socket.
     * 
     * @param flag Flag controlling behavior of the receive operation
     * @return The message bytes
     */
    byte[] receive(MessageFlag flag);

    /**
     * Receive a message part from a socket.
     * 
     * @param buf The byte buffer
     * @param offset The buffer offset
     * @param length The buffer length
     * @param flag Flag controlling behavior of the receive operation
     * @return The number of bytes read, -1 on error
     */
    int receive(byte[] buf, int offset, int len, MessageFlag flag);

    /**
     * Receive a message part from a socket into a byte buffer.
     * 
     * @param buf The byte buffer
     * @param flag Flag controlling behavior of the receive operation
     * @return The number of bytes read, -1 on error
     */
    int receiveByteBuffer(ByteBuffer buf, MessageFlag flag);

    /**
     * This receiver has more of a multi-part message waiting for receipt.
     * 
     * @return true if there is more data, false otherwise
     */
    boolean hasMoreToReceive();

    /**
     * Receive the full message (all frames) from the socket.
     * 
     * @return The full message (all frames) from the socket.
     */
    Message receiveMessage();

    /**
     * Receive the full message (all frames) from the socket.
     * 
     * @param flag Flag controlling behavior of the receive operation
     * @return The full message (all frames) from the socket.
     */
    Message receiveMessage(MessageFlag flag);

    /**
     * Receive a routed message (all frames) from the socket.
     * 
     * @return The full message (all frames) from the socket, assuming it has been through a Router socket, and has
     * routing frames associated with it.
     */
    RoutedMessage receiveRoutedMessage();

    /**
     * Receive a routed message (all frames) from the socket.
     * 
     * @param flag Flag controlling behavior of the receive operation
     * @return The full message (all frames) from the socket, assuming it has been through a Router socket, and has
     * routing frames associated with it.
     */
    RoutedMessage receiveRoutedMessage(MessageFlag flag);
}
