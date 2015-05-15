package org.zeromq.api;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.zeromq.ContextFactory;
import org.zeromq.api.Message.Frame;

public class MessageTest {

    private Context context;

    @Before
    public void setUp() {
        this.context = ContextFactory.createContext(1);
    }

    @After
    public void tearDown() {
        context.close();
    }

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

    @Test
    @Ignore("The JZMQ and JeroMQ libraries differ in how closed sockets behave - JeroMQ sometimes can hang, while JZMQ returns null")
    public void testClosedSocket() throws Exception {
        Socket pub = context.buildSocket(SocketType.PUB)
                .bind("inproc://message-test");
        Socket sub = context.buildSocket(SocketType.SUB)
                .asSubscribable()
                .subscribe("".getBytes())
                .connect("inproc://message-test");

        pub.send("hello".getBytes());

        sub.close();
        assertNull(sub.receiveMessage());
    }

}
