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
    public static final byte[] EMPTY_FRAME_DATA = new byte[0];

    /** A handy reference to an empty {@link Frame}. */
    public static final Frame EMPTY_FRAME = new Frame(EMPTY_FRAME_DATA);

    private final Deque<Frame> frames = new ArrayDeque<Frame>();

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
        this(new Frame(firstFrame));
    }

    /**
     * Construct a message with a single byte[] frame.
     * 
     * @param firstFrame The first frame in the new message
     */
    public Message(byte[] firstFrame) {
        this(new Frame(firstFrame));
    }

    /**
     * Construct a message with a single frame.
     *
     * @param firstFrame The first frame in the new message
     */
    public Message(int firstFrame) {
        this(Frame.wrap(firstFrame));
    }

    /**
     * Construct a message with a single frame.
     *
     * @param firstFrame The first frame in the new message
     */
    public Message(long firstFrame) {
        this(Frame.wrap(firstFrame));
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

    public List<Frame> getFrames() {
        return new ArrayList<>(frames);
    }

    /**
     * Add a frame to the end of the list.
     * 
     * @param frame The frame to be added
     */
    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    /**
     * Add an empty frame to the end of the list.
     */
    public void addEmptyFrame() {
        frames.add(EMPTY_FRAME);
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
     */
    public void pushFrame(Frame frame) {
        frames.push(frame);
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
     * Add the frames to the end of the list.
     * 
     * @param frames The frames to be added
     */
    public void addFrames(List<Frame> frames) {
        this.frames.addAll(frames);
    }

    /**
     * Add all frames from the given message to the end of the list.
     * 
     * @param payload The original message, containing frames to be added
     */
    public void addFrames(Message payload) {
        frames.addAll(payload.frames);
    }

    /**
     * Add frames to the end of the list, in reverse order, such that the
     * elements are in the same order as in the original message.
     * 
     * @param frames The frames to be added
     */
    public void pushFrames(List<Frame> frames) {
        ListIterator<Frame> itr = frames.listIterator();
        while (itr.hasPrevious()) {
            this.frames.push(itr.previous());
        }
    }

    /**
     * Add frames to the beginning of the list, in reverse order, such that
     * the elements are in the same order as in the original message.
     * 
     * @param payload The original message, containing frames to be added
     */
    public void pushFrames(Message payload) {
        Iterator<Frame> itr = payload.frames.descendingIterator();
        while (itr.hasNext()) {
            frames.push(itr.next());
        }
    }

    /**
     * Remove a frame from the beginning of the list and convert to an {@code int}.
     * 
     * @return The next frame, as an integer
     */
    public int intValue() {
        return popFrame().getInt();
    }

    /**
     * Remove a frame from the beginning of the list and convert to a {@code long}.
     *
     * @return The next frame, as a long
     */
    public long longValue() {
        return popFrame().getLong();
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
        return (frames.size() == 0);
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
     * class are immutable.
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

        /**
         * Construct an empty frame with the given capacity.
         * 
         * @param capacity The capacity of the underlying buffer
         */
        public Frame(int capacity) {
            this(ByteBuffer.allocate(capacity));
        }

        /**
         * Construct a frame with the given buffer.
         *
         * @param buffer The underlying buffer
         */
        public Frame(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        /**
         * Returns the data contained in this frame, as bytes.
         * 
         * @return The frame's data
         */
        public byte[] getData() {
            byte[] buf = buffer.array();
            if (buffer.position() > 0 && buffer.position() != buffer.capacity()) {
                buf = new byte[buffer.position()];
                System.arraycopy(buffer.array(), 0, buf, 0, buffer.position());
            }
            return buf;
        }

        /**
         * Returns the underlying buffer.
         * 
         * @return The underlying buffer
         */
        public ByteBuffer getBuffer() {
            return buffer;
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

        public byte getByte() {
            return buffer.get();
        }

        public Frame putByte(byte value) {
            buffer.put(value);
            return this;
        }

        public short getShort() {
            return buffer.getShort();
        }

        public Frame putShort(short value) {
            buffer.putShort(value);
            return this;
        }

        public int getInt() {
            return buffer.getInt();
        }

        public Frame putInt(int value) {
            buffer.putInt(value);
            return this;
        }

        public long getLong() {
            return buffer.getLong();
        }

        public Frame putLong(long value) {
            buffer.putLong(value);
            return this;
        }

        public String getChars() {
            int len = buffer.getShort();
            byte[] buf = new byte[len];
            buffer.get(buf);
            return new String(buf, CHARSET);
        }

        public Frame putChars(String value) {
            buffer.putShort((short) value.length());
            buffer.put(value.getBytes(CHARSET));
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Frame frame = (Frame) o;

            if (!buffer.equals(frame.buffer)) return false;

            return true;
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
            if (buffer == null) {
                return "Frame{data=null}";
            }
            return "Frame{data=" + getString() + '}';
        }

        public boolean isBlank() {
            return (buffer.capacity() == 0);
        }

        public static Frame wrap(byte value) {
            return new Frame(1).putByte(value);
        }

        public static Frame wrap(short value) {
            return new Frame(2).putShort(value);
        }

        public static Frame wrap(int value) {
            return new Frame(4).putInt(value);
        }

        public static Frame wrap(long value) {
            return new Frame(8).putLong(value);
        }
    }
}
