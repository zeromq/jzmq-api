package org.zeromq.api;

import org.zeromq.api.Message.Frame;

/**
 * Buffer capable of sending/receiving a 4-byte integer (Java {@code int}). 
 */
public class ZInteger {
    private final byte[] buf = new byte[4];

    /**
     * Construct an empty buffer (defaults to 0).
     */
    public ZInteger() {
    }

    /**
     * Construct a buffer with a default value.
     *
     * @param val The default value
     */
    public ZInteger(int val) {
        put(val);
    }

    /**
     * Constrct a buffer with a default value.
     *
     * @param frame A frame of data
     */
    public ZInteger(byte[] frame) {
        put(frame, 0);
    }

    /**
     * Constrct a buffer with a default value.
     *
     * @param frame A frame of data
     * @param offset Offset within the given array
     */
    public ZInteger(byte[] frame, int offset) {
        put(frame, offset);
    }

    /**
     * Constrct a buffer with a default value.
     *
     * @param frame A frame of data
     */
    public ZInteger(Frame frame) {
        put(frame.getData(), 0);
    }

    /**
     * Receive a 4-byte value as a frame of data on the given Socket.
     * 
     * @param socket The Socket to read from
     * @return The integer value received from the Socket
     */
    public int receive(Socket socket) {
        socket.receive(buf, 0, buf.length, MessageFlag.NONE);
        return Bits.getInt(buf);
    }

    /**
     * Send a 4-byte value as a frame of data on the given Socket.
     * 
     * @param socket The socket to send to
     */
    public void send(Socket socket) {
        socket.send(buf);
    }

    /**
     * Set a value on the buffer.
     * 
     * @param val The integer value
     * @return This object, with the value set
     */
    public ZInteger put(int val) {
        Bits.putInt(buf, val);
        return this;
    }

    /**
     * Set a value on the buffer.
     *
     * @param frame A frame of data
     * @param offset Offset within the given array
     * @return This object, with the value set
     */
    public ZInteger put(byte[] frame, int offset) {
        assert (frame.length >= 4);
        System.arraycopy(frame, offset, buf, 0, 4);
        return this;
    }

    /**
     * Construct a Frame from the data contained in this buffer.
     * 
     * @return A Frame containing the data in this buffer
     */
    public Frame frame() {
        byte[] newbuf = new byte[4];
        System.arraycopy(buf, 0, newbuf, 0, 4);
        return new Frame(newbuf);
    }

    /**
     * Returns the value of this buffer as an {@code int}.
     * 
     * @return  the numeric value represented by this object after conversion
     *          to type {@code int}.
     */
    public int intValue() {
        return Bits.getInt(buf);
    }

    /**
     * Returns the value of this buffer as a {@code long}.
     * 
     * @return  the numeric value represented by this object after conversion
     *          to type {@code long}.
     */
    public long longValue() {
        return intValue();
    }
}
