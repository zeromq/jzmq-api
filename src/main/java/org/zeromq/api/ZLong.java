package org.zeromq.api;

import org.zeromq.api.Message.Frame;

/**
 * Buffer capable of sending/receiving an 8-byte integer (Java {@code long}). 
 */
public class ZLong {
    private final byte[] buf = new byte[8];

    /**
     * Construct an empty buffer (defaults to 0).
     */
    public ZLong() {
    }

    /**
     * Construct a buffer with a default value.
     * 
     * @param val The default value
     */
    public ZLong(long val) {
        put(val);
    }

    /**
     * Constrct a buffer with a default value.
     * 
     * @param frame A frame of data
     */
    public ZLong(byte[] frame) {
        put(frame, 0);
    }

    /**
     * Constrct a buffer with a default value.
     *
     * @param frame A frame of data
     * @param offset Offset within the given array
     */
    public ZLong(byte[] frame, int offset) {
        put(frame, offset);
    }

    /**
     * Constrct a buffer with a default value.
     *
     * @param frame A frame of data
     */
    public ZLong(Frame frame) {
        put(frame.getData(), 0);
    }

    /**
     * Receive an 8-byte value as a frame of data on the given Socket.
     * 
     * @param socket The Socket to read from
     * @return The integer value received from the Socket
     */
    public long receive(Socket socket) {
        socket.receive(buf, 0, buf.length, MessageFlag.NONE);
        return longValue();
    }

    /**
     * Send an 8-byte value as a frame of data on the given Socket.
     * 
     * @param socket The socket to send to
     */
    public void send(Socket socket) {
        socket.send(buf);
    }

    /**
     * Set a value on the buffer.
     * 
     * @param val The long value
     * @return This object, with the value set
     */
    public ZLong put(long val) {
        Bits.putLong(buf, val);
        return this;
    }

    /**
     * Set a value on the buffer.
     * 
     * @param frame A frame of data
     * @param offset Offset within the given array
     * @return This object, with the value set
     */
    public ZLong put(byte[] frame, int offset) {
        assert (frame.length >= 8);
        System.arraycopy(frame, offset, buf, 0, 8);
        return this;
    }

    /**
     * Construct a Frame from the data contained in this buffer.
     * 
     * @return A Frame containing the data in this buffer
     */
    public Frame frame() {
        byte[] newbuf = new byte[8];
        System.arraycopy(buf, 0, newbuf, 0, 8);
        return new Frame(newbuf);
    }

    /**
     * Returns the value of this buffer as an {@code int}.
     * 
     * @return  the numeric value represented by this object after conversion
     *          to type {@code int}.
     */
    public int intValue() {
        return (int) longValue();
    }

    /**
     * Returns the value of this buffer as a {@code long}.
     * 
     * @return  the numeric value represented by this object after conversion
     *          to type {@code long}.
     */
    public long longValue() {
        return Bits.getLong(buf);
    }
}
