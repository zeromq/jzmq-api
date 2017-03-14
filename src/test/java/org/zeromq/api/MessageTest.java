package org.zeromq.api;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import java.nio.ByteBuffer;
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
        List<Frame> frames = testClass.getFrames();
        assertEquals(1, frames.size());
        assertArrayEquals(new byte[]{5, 6, 7}, frames.get(0).getData());
    }

    @Test
    public void testBlankFrame() throws Exception {
        Message testClass = new Message();
        testClass.addEmptyFrame();
        List<Frame> frames = testClass.getFrames();
        assertEquals(1, frames.size());
        assertArrayEquals(new byte[0], frames.get(0).getData());
    }

    @Test
    public void testMixedFrames() throws Exception {
        Message testClass = new Message();
        testClass.addEmptyFrame();
        testClass.addFrame(Frame.of("Hello"));
        List<Frame> frames = testClass.getFrames();
        assertEquals(2, frames.size());
        assertArrayEquals(new byte[0], frames.get(0).getData());
        assertArrayEquals("Hello".getBytes(), frames.get(1).getData());
    }

    @Test
    public void testCopyConstructor() throws Exception {
        Message initial = new Message();
        initial.addFrame(Frame.of("hello"));
        initial.addEmptyFrame();
        initial.addFrame(Frame.of("goodbye"));

        Message newMessage = new Message(initial);
        assertEquals(initial.getFrames(), newMessage.getFrames());
    }

    @Test
    public void testPutInt_100() {
        Frame frame = Frame.of(100);

        byte[] buf = frame.getData();
        assertEquals(0, buf[0]);
        assertEquals(0, buf[1]);
        assertEquals(0, buf[2]);
        assertEquals(100, buf[3]);
    }

    @Test
    public void testGetInt_100() {
        byte[] buf = {0, 0, 0, 100};
        Frame frame = new Frame(buf);

        assertEquals(100, frame.getInt());
    }

    @Test
    public void testPutInt_0x77777777() {
        Frame frame = Frame.of(0x77777777);

        byte[] buf = frame.getData();
        assertEquals(0x77, buf[0]);
        assertEquals(0x77, buf[1]);
        assertEquals(0x77, buf[2]);
        assertEquals(0x77, buf[3]);
    }

    @Test
    public void testGetInt_0x77777777() {
        byte[] buf = {0x77, 0x77, 0x77, 0x77};
        Frame frame = new Frame(buf);

        assertEquals(0x77777777, frame.getInt());
    }

    @Test
    public void testPutLong_100() {
        Frame frame = Frame.of(100L);

        byte[] buf = frame.getData();
        assertEquals(0, buf[0]);
        assertEquals(0, buf[1]);
        assertEquals(0, buf[2]);
        assertEquals(0, buf[3]);
        assertEquals(0, buf[4]);
        assertEquals(0, buf[5]);
        assertEquals(0, buf[6]);
        assertEquals(100L, buf[7]);
    }

    @Test
    public void testGetLong_100() {
        byte[] buf = {0, 0, 0, 0, 0, 0, 0, 100};
        Frame frame = new Frame(buf);

        assertEquals(100, frame.getLong());
    }

    @Test
    public void testPutLong_0x77777777() {
        Frame frame = Frame.of(0x7777777777777777L);

        byte[] buf = frame.getData();
        assertEquals(0x77, buf[0]);
        assertEquals(0x77, buf[1]);
        assertEquals(0x77, buf[2]);
        assertEquals(0x77, buf[3]);
        assertEquals(0x77, buf[4]);
        assertEquals(0x77, buf[5]);
        assertEquals(0x77, buf[6]);
        assertEquals(0x77, buf[7]);
    }

    @Test
    public void testGetLong_0x77777777() {
        byte[] buf = {0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77};
        Frame frame = new Frame(buf);

        assertEquals(0x7777777777777777L, frame.getLong());
    }

    @Test
    public void testPutChars() {
        String string = "Hello, world!";
        Frame frame = new Message.FrameBuilder(10).putString(string).putString(string).build();

        ByteBuffer buffer = ByteBuffer.wrap(frame.getData());
        buffer.rewind();
        assertEquals(string.length(), buffer.get());

        byte[] buf = new byte[string.length()];
        buffer.get(buf);
        assertEquals(string, new String(buf, Message.CHARSET));

        assertEquals(string.length(), buffer.get());
        buffer.get(buf);
        assertEquals(string, new String(buf, Message.CHARSET));
    }

    @Test
    public void testGetString() {
        String string = "Hello, world!";
        ByteBuffer buffer = ByteBuffer.allocate(string.length() + 2);
        buffer.put((byte) string.length());
        buffer.put(string.getBytes(Message.CHARSET));
        buffer.rewind();

        Frame frame = new Frame(buffer.array());
        assertEquals(string, frame.getString());
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
