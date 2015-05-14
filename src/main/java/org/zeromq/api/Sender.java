package org.zeromq.api;

import java.nio.ByteBuffer;

/**
 * Socket capable of sending message parts.
 */
public interface Sender {
    /**
     * Send a message part on a socket.
     * 
     * @param buf The message bytes
     * @return true if the operation was successful, false otherwise
     */
    boolean send(byte[] buf);

    /**
     * Send a message part on a socket.
     * 
     * @param buf The message bytes
     * @param flag Flag controlling behavior of the send operation
     * @return true if the operation was successful, false otherwise
     */
    boolean send(byte[] buf, MessageFlag flag);

    /**
     * Send a message part on a socket.
     * 
     * @param buf The message bytes
     * @param offset The buffer offset
     * @param length The buffer length
     * @param flag Flag controlling behavior of the send operation
     * @return true if the operation was successful, false otherwise
     */
    boolean send(byte[] buf, int offset, int length, MessageFlag flag);

    /**
     * Send a message part on a socket from a byte buffer.
     * 
     * @param buf The byte buffer
     * @param flag Flag controlling behavior of the send operation
     * @return true if the operation was successful, false otherwise
     */
    boolean sendByteBuffer(ByteBuffer buf, MessageFlag flag);

    /**
     * Send the full message (all frames) on the socket.
     * 
     * @param message The full message
     * @return true if the operation was successful, false otherwise
     */
    boolean send(Message message);
}
