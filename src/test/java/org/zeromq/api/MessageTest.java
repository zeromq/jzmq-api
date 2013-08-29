package org.zeromq.api;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.junit.Test;
import org.zeromq.api.Message.Frame;

public class MessageTest {

    @Test
    public void testAddFrame() throws Exception {
        Message testClass = new Message();
        testClass.addFrame(new Frame(new byte[]{5, 6, 7}));
        List<Message.Frame> frames = testClass.getFrames();
        assertEquals(1, frames.size());
        assertArrayEquals(new byte[]{5, 6, 7}, frames.get(0).getData());
    }

    @Test
    public void testBlankFrame() throws Exception {
        Message testClass = new Message();
        testClass.addEmptyFrame();
        List<Message.Frame> frames = testClass.getFrames();
        assertEquals(1, frames.size());
        assertArrayEquals(new byte[0], frames.get(0).getData());
    }

    @Test
    public void testMixedFrames() throws Exception {
        Message testClass = new Message();
        testClass.addEmptyFrame();
        testClass.addFrame(new Frame("Hello"));
        List<Message.Frame> frames = testClass.getFrames();
        assertEquals(2, frames.size());
        assertArrayEquals(new byte[0], frames.get(0).getData());
        assertArrayEquals("Hello".getBytes(), frames.get(1).getData());
    }

    @Test
    public void testCopyConstructor() throws Exception {
        Message initial = new Message();
        initial.addFrame(new Frame("hello"));
        initial.addEmptyFrame();
        initial.addFrame(new Frame("goodbye"));

        Message newMessage = new Message(initial);
        assertEquals(initial.getFrames(), newMessage.getFrames());
    }

}
