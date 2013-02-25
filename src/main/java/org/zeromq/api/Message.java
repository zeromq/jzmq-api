package org.zeromq.api;

import java.util.*;

//NOT THREAD SAFE
public class Message {

    public static final byte[] EMPTY_FRAME_DATA = new byte[0];
    private final Deque<Frame> frames = new ArrayDeque<Frame>();

    public Message() {
    }

    public Message(byte[] firstFrame) {
        if (firstFrame != null) {
            frames.add(new Frame(firstFrame));
        }
    }

    public Message(List<Frame> frames) {
        this.frames.addAll(frames);
    }

    public Message(Message message) {
        frames.addAll(message.getFrames());
    }

    public List<Frame> getFrames() {
        return new ArrayList<Frame>(frames);
    }

    public void addFrame(byte[] data) {
        if (data == null) {
            return;
        }
        frames.add(new Frame(data));
    }

    public void addEmptyFrame() {
        frames.add(new Frame(EMPTY_FRAME_DATA));
    }

    public byte[] getFirstFrame() {
        return frames.getFirst().data;
    }

    public void addFrames(Message payload) {
        frames.addAll(payload.getFrames());
    }

    public boolean isMissing() {
        return frames.size() == 0;
    }

    public int size() {
        return frames.size();
    }

    protected Frame popFrame() {
        return frames.pop();
    }

    public static class Frame {
        private final byte[] data;

        Frame(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        public int size() {
            return data.length;
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
