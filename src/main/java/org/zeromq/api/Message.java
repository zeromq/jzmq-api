package org.zeromq.api;

import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
        return new ArrayList<Frame>(frames);
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
     * @param payload The frames to be added
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
     * An alias for {@link #isEmpty()}.
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
        private final byte[] data;

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
            this.data = data;
        }

        /**
         * Returns the data contained in this frame, as bytes.
         * 
         * @return The frame's data
         */
        public byte[] getData() {
            return data;
        }

        /**
         * Returns the size of the underlying byte array.
         * 
         * @return The frame's size
         */
        public int size() {
            return data.length;
        }

        /**
         * Convert the data to a string using the configured {@link Charset}.
         * 
         * @return The frame, as a String
         * @see Message#CHARSET
         */
        public String getString() {
            return new String(data, CHARSET);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Frame frame = (Frame) o;

            if (!Arrays.equals(data, frame.data)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (data != null) ? Arrays.hashCode(data) : 0;
        }

        /**
         * Convert the frame to a string, for use in debugging.
         */
        @Override
        public String toString() {
            if (data == null) {
                return "Frame{data=null}";
            }
            return "Frame{data=" + new String(data, CHARSET) + '}';
        }

        public boolean isBlank() {
            return (data.length == 0);
        }
    }

}
