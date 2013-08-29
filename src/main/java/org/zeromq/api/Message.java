package org.zeromq.api;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Message containing frames for sending data via a socket.
 */
public class Message implements Iterable<Message.Frame> {

    public static final byte[] EMPTY_FRAME_DATA = new byte[0];
    private final Deque<Frame> frames = new ArrayDeque<Frame>();

    public Message() {
    }

    public Message(String firstFrame) {
        this(new Frame(firstFrame));
    }

    public Message(byte[] firstFrame) {
        this(new Frame(firstFrame));
    }

    public Message(Frame firstFrame) {
        addFrame(firstFrame);
    }

    public Message(List<Frame> frames) {
        addFrames(frames);
    }

    public Message(Message message) {
        addFrames(message);
    }

    public List<Frame> getFrames() {
        return new ArrayList<Frame>(frames);
    }

    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    public void addEmptyFrame() {
        frames.add(new Frame(EMPTY_FRAME_DATA));
    }

    public Frame getFirstFrame() {
        return frames.getFirst();
    }

    public void pushFrame(Frame frame) {
        frames.push(frame);
    }

    public Frame popFrame() {
        return frames.pop();
    }

    public void addFrames(List<Frame> frames) {
        this.frames.addAll(frames);
    }

    public void addFrames(Message payload) {
        frames.addAll(payload.frames);
    }

    public void pushFrames(Message payload) {
        Iterator<Frame> itr = payload.frames.descendingIterator();
        while (itr.hasNext()) {
            frames.push(itr.next());
        }
    }

    public boolean isMissing() {
        return frames.size() == 0;
    }

    public int size() {
        return frames.size();
    }

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

    public static class Frame {
        private final byte[] data;

        public Frame(String data) {
            this(data.getBytes());
        }

        public Frame(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        public int size() {
            return data.length;
        }

        public String getString() {
            return new String(data);
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
            return data != null ? Arrays.hashCode(data) : 0;
        }

        @Override
        public String toString() {
            if (data == null) {
                return "Frame{data=null}";
            }
            return "Frame{data=" + new String(data) + '}';
        }

        public boolean isBlank() {
            return data.length == 0;
        }
    }

}
