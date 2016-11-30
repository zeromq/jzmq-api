package org.zeromq.api;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Message containing frames for sending data via a socket.
 */
public class Message implements Iterable<Message.Frame> {

    /** Default charset, to be customized by the application, or set via property. */
    public static Charset CHARSET = Charset.forName(
            System.getProperty("zmq.default.charset", "UTF-8"));

    /** An empty byte array. */
    private static final byte[] EMPTY_FRAME_DATA = new byte[0];

    /** A handy reference to an empty {@link Frame}. */
    public static final Frame EMPTY_FRAME = new Frame(EMPTY_FRAME_DATA);

    private final Deque<Frame> frames = new ArrayDeque<>();

    /**
     * Construct an empty message.
     */
    public Message() {
    }

    /**
     * Construct a message with a single String frame.
     * 
     * @param firstFrame The first frame in the new message
     */
    public Message(String firstFrame) {
        addString(firstFrame);
    }

    /**
     * Construct a message with a single byte[] frame.
     * 
     * @param firstFrame The first frame in the new message
     */
    public Message(byte[] firstFrame) {
        addBytes(firstFrame);
    }

    /**
     * Construct a message with a single frame.
     *
     * @param firstFrame The first frame in the new message
     */
    public Message(int firstFrame) {
        addInt(firstFrame);
    }

    /**
     * Construct a message with a single frame.
     *
     * @param firstFrame The first frame in the new message
     */
    public Message(long firstFrame) {
        addLong(firstFrame);
    }

    /**
     * Construct a message with a single frame.
     * 
     * @param firstFrame The first frame in the new message
     */
    public Message(Frame firstFrame) {
        addFrame(firstFrame);
    }

    /**
     * Construct a message with the given frames.
     * 
     * @param frames The initial list of frames to populate the message
     */
    public Message(List<Frame> frames) {
        addFrames(frames);
    }

    /**
     * Construct a message by cloning the given message's frames.
     * 
     * @param message The original message to clone
     */
    public Message(Message message) {
        addFrames(message);
    }

    /**
     * Return a copy of the list of frames contained in this message.
     * 
     * @return The frames of this message
     */
    public List<Frame> getFrames() {
        return new ArrayList<>(frames);
    }

    /**
     * Add a frame to the end of the list.
     * 
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message addFrame(Frame frame) {
        frames.add(frame);
        return this;
    }

    /**
     * Add an empty frame to the end of the list.
     * 
     * @return This message, for method chaining
     */
    public Message addEmptyFrame() {
        frames.add(EMPTY_FRAME);
        return this;
    }

    /**
     * Return the first frame in the list.
     * 
     * @return The first frame
     */
    public Frame getFirstFrame() {
        return frames.getFirst();
    }

    /**
     * Add a frame to the beginning of the list.
     * 
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message pushFrame(Frame frame) {
        frames.push(frame);
        return this;
    }

    /**
     * Remove a frame from the beginning of the list.
     * 
     * @return The first frame
     */
    public Frame popFrame() {
        return frames.pop();
    }

    /**
     * Add a frame containing a String value to the end of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message addString(String frame) {
        return addFrame(new Frame(frame));
    }

    /**
     * Add a frame containing a String value to the beginning of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message pushString(String frame) {
        return pushFrame(new Frame(frame));
    }

    /**
     * Remove a frame from the beginning of the list.
     *
     * @return The first frame, as a String
     */
    public String popString() {
        return popFrame().getString();
    }

    /**
     * Add a frame containing an int value to the end of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message addInt(int frame) {
        return addFrame(Frame.wrap(frame));
    }

    /**
     * Add a frame containing an int value to the beginning of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message pushInt(int frame) {
        return pushFrame(Frame.wrap(frame));
    }

    /**
     * Remove a frame from the beginning of the list and convert to an {@code int}.
     *
     * @return The first frame, as an integer
     */
    public int popInt() {
        return popFrame().getInt();
    }

    /**
     * Add a frame containing a long value to the end of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message addLong(long frame) {
        return addFrame(Frame.wrap(frame));
    }

    /**
     * Add a frame containing a long value to the beginning of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message pushLong(long frame) {
        return pushFrame(Frame.wrap(frame));
    }

    /**
     * Remove a frame from the beginning of the list and convert to a {@code long}.
     *
     * @return The first frame, as a long
     */
    public long popLong() {
        return popFrame().getLong();
    }

    /**
     * Add a frame containing a short value to the end of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message addShort(short frame) {
        return addFrame(Frame.wrap(frame));
    }

    /**
     * Add a frame containing a short value to the beginning of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message pushShort(short frame) {
        return pushFrame(Frame.wrap(frame));
    }

    /**
     * Remove a frame from the beginning of the list and convert to a {@code short}.
     *
     * @return The first frame, as a short
     */
    public short popShort() {
        return popFrame().getShort();
    }

    /**
     * Add a frame containing a byte to the end of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message addByte(byte frame) {
        return addFrame(Frame.wrap(frame));
    }

    /**
     * Add a frame containing a byte to the beginning of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message pushByte(byte frame) {
        return pushFrame(Frame.wrap(frame));
    }

    /**
     * Remove a frame from the beginning of the list and convert to a {@code byte}.
     *
     * @return The first frame, as a byte
     */
    public byte popByte() {
        return popFrame().getByte();
    }

    /**
     * Add a frame to the end of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message addBuffer(ByteBuffer frame) {
        return addFrame(new Frame(frame));
    }

    /**
     * Add a frame to the beginning of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message pushBuffer(ByteBuffer frame) {
        return pushFrame(new Frame(frame));
    }

    /**
     * Add a frame to the end of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message addBytes(byte[] frame) {
        return addFrame(new Frame(frame));
    }

    /**
     * Add a frame to the beginning of the list.
     *
     * @param frame The frame to be added
     * @return This message, for method chaining
     */
    public Message pushBytes(byte[] frame) {
        return pushFrame(new Frame(frame));
    }

    /**
     * Remove a frame from the beginning of the list and convert to a {@code byte[]}.
     * 
     * @return The first frame, as a byte array
     */
    public byte[] popBytes() {
        return popFrame().getData();
    }

    /**
     * Add the frames to the end of the list.
     * 
     * @param frames The frames to be added
     * @return This message, for method chaining
     */
    public Message addFrames(List<Frame> frames) {
        this.frames.addAll(frames);
        return this;
    }

    /**
     * Add all frames from the given message to the end of the list.
     * 
     * @param payload The original message, containing frames to be added
     * @return This message, for method chaining
     */
    public Message addFrames(Message payload) {
        frames.addAll(payload.frames);
        return this;
    }

    /**
     * Add frames to the end of the list, in reverse order, such that the
     * elements are in the same order as in the original message.
     * 
     * @param frames The frames to be added
     * @return This message, for method chaining
     */
    public Message pushFrames(List<Frame> frames) {
        ListIterator<Frame> itr = frames.listIterator(frames.size());
        while (itr.hasPrevious()) {
            this.frames.push(itr.previous());
        }

        return this;
    }

    /**
     * Add frames to the beginning of the list, in reverse order, such that
     * the elements are in the same order as in the original message.
     * 
     * @param payload The original message, containing frames to be added
     * @return This message, for method chaining
     */
    public Message pushFrames(Message payload) {
        Iterator<Frame> itr = payload.frames.descendingIterator();
        while (itr.hasNext()) {
            frames.push(itr.next());
        }

        return this;
    }

    /**
     * An alias for {@link #isEmpty()}.
     *
     * @return true if the message is empty, false otherwise
     */
    public boolean isMissing() {
        return isEmpty();
    }

    /**
     * Returns <code>true</code> if this message contains no frames.
     * 
     * @return true if the message is empty, false otherwise
     */
    public boolean isEmpty() {
        return frames.isEmpty();
    }

    /**
     * Returns the number of frames in this message.
     * 
     * @return The number of frames in this message
     */
    public int size() {
        return frames.size();
    }

    /**
     * Returns an iterator over the frames in this message.
     * 
     * @return An iterator over the frames in this message
     */
    @Override
    public Iterator<Frame> iterator() {
        return frames.iterator();
    }

    /**
     * Dump the message in human readable format.
     * <p>
     * This should only be used for debugging and tracing, inefficient in
     * handling large messages.
     * 
     * @return A StringBuilder with the message in human readable format
     */
    protected StringBuilder dump(StringBuilder sb) {
        for (Frame frame : frames) {
            if (sb.length() > 0) {
                sb.append(", ");
            } else {
                sb.append("Frames{");
            }
            sb.append(frame.toString());
        }
        sb.append("}");
        return sb;
    }

    /**
     * Convert the message to a string, for use in debugging.
     */
    @Override
    public String toString() {
        return dump(new StringBuilder()).toString();
    }

    /**
     * Represents a single frame of data within a message. Instances of this
     * class are immutable, except via the underlying {@link ByteBuffer}.
     */
    public static class Frame {
        private final ByteBuffer buffer;

        /**
         * Construct a frame with the given String, using the configured
         * {@link Charset}.
         * 
         * @param data The String data
         * @see Message#CHARSET
         */
        public Frame(String data) {
            this(data.getBytes(CHARSET));
        }

        /**
         * Construct a frame with the given bytes.
         * 
         * @param data The data
         */
        public Frame(byte[] data) {
            this(data == null ? null : ByteBuffer.wrap(data));
        }

        private Frame(ByteBuffer buffer) {
            if (buffer == null) {
                throw new NullPointerException("Data cannot be null");
            }

            this.buffer = buffer;
        }

        /**
         * Returns the data contained in this frame, as bytes.
         * 
         * @return The frame's data
         */
        public byte[] getData() {
            byte[] buf = buffer.array(); // violates immutability, but improves speed for the common case
            if (buffer.position() > 0 && buffer.position() != buffer.capacity()) {
                buf = new byte[buffer.position()];
                System.arraycopy(buffer.array(), 0, buf, 0, buffer.position());
            }
            return buf;
        }

        /**
         * Returns the size of the underlying byte array.
         * 
         * @return The frame's size
         */
        public int size() {
            return buffer.capacity();
        }

        /**
         * Convert the data to a string using the configured {@link Charset}.
         * 
         * @return The frame, as a String
         * @see Message#CHARSET
         */
        public String getString() {
            return new String(buffer.array(), CHARSET);
        }

        /**
         * Returns the next {@code byte} of data from the buffer.
         * 
         * @return The next byte of data
         */
        public byte getByte() {
            return buffer.get();
        }

        /**
         * Returns the next 2 bytes of data, as a {@code short}.
         * 
         * @return The next 2 bytes, as a short
         */
        public short getShort() {
            return buffer.getShort();
        }

        /**
         * Returns the next 4 bytes of data, as a {@code int}.
         * 
         * @return The next 4 bytes, as an int
         */
        public int getInt() {
            return buffer.getInt();
        }

        /**
         * Returns the next 8 bytes of data, as a {@code long}.
         * 
         * @return The next 8 bytes, as a long
         */
        public long getLong() {
            return buffer.getLong();
        }

        /**
         * Returns a String value encoded into the buffer as a short and bytes
         * using the default character set.
         * 
         * @return A String value
         */
        public String getChars() {
            int len = buffer.getShort();
            byte[] buf = new byte[len];
            buffer.get(buf);
            return new String(buf, CHARSET);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Frame frame = (Frame) o;
            return buffer.equals(frame.buffer);
        }

        @Override
        public int hashCode() {
            return buffer.hashCode();
        }

        /**
         * Convert the frame to a string, for use in debugging.
         */
        @Override
        public String toString() {
            return "Frame{data=" + getString() + '}';
        }

        /**
         * Returns true if the buffer contains no data.
         * 
         * @return true if the frame contains no data, false otherwise
         */
        public boolean isBlank() {
            return buffer.capacity() == 0;
        }

        public static Frame wrap(byte value) {
            return new FrameBuilder(1).putByte(value).build();
        }

        public static Frame wrap(short value) {
            return new FrameBuilder(2).putShort(value).build();
        }

        public static Frame wrap(int value) {
            return new FrameBuilder(4).putInt(value).build();
        }

        public static Frame wrap(long value) {
            return new FrameBuilder(8).putLong(value).build();
        }
    }

    /**
     * Builder for constructing efficient binary representations of multi-value
     * data frames using a dynamically sized buffer.
     */
    public static class FrameBuilder {
        private ByteBuffer buffer;

        public FrameBuilder(int capacity) {
            this.buffer = ByteBuffer.allocate(capacity);
        }

        public FrameBuilder() {
            this.buffer = ByteBuffer.allocate(32);
        }

        /**
         * Put a {@code byte} of data into the buffer.
         *
         * @param value A byte of data
         * @return This builder, for method chaining
         */
        public FrameBuilder putByte(byte value) {
            checkCapacity(1);
            buffer.put(value);
            return this;
        }

        /**
         * Put a {@code short} into the buffer.
         *
         * @param value A short value
         * @return This builder, for method chaining
         */
        public FrameBuilder putShort(short value) {
            checkCapacity(2);
            buffer.putShort(value);
            return this;
        }

        /**
         * Put a {@code int} into the buffer.
         *
         * @param value An int value
         * @return This builder, for method chaining
         */
        public FrameBuilder putInt(int value) {
            checkCapacity(4);
            buffer.putInt(value);
            return this;
        }

        /**
         * Put a {@code long} into the buffer.
         *
         * @param value A long value
         * @return This builder, for method chaining
         */
        public FrameBuilder putLong(long value) {
            checkCapacity(8);
            buffer.putLong(value);
            return this;
        }

        /**
         * Put an encoded String value into the buffer as a short and bytes
         * using the default character set.
         *
         * @param value A String value
         * @return This builder, for method chaining
         */
        public FrameBuilder putChars(String value) {
            byte[] bytes = value.getBytes(CHARSET);
            checkCapacity(bytes.length + 2);
            buffer.putShort((short) bytes.length);
            buffer.put(bytes);
            return this;
        }

        /**
         * Build a frame containing the data in the underlying buffer.
         *
         * @return The new frame
         */
        public Frame build() {
            return new Frame(buffer);
        }

        private void checkCapacity(int neededBytes) {
            int capacity = buffer.capacity();
            int position = buffer.position();
            if (capacity - position < neededBytes) {
                buffer = ByteBuffer.allocate(Math.max(capacity * 2, capacity + neededBytes)).put(buffer.array());
            }
        }
    }
}
