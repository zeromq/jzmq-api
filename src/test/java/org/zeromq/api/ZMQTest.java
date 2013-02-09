package org.zeromq.api;

import junit.framework.TestCase;
import org.junit.Test;
import org.zeromq.ContextFactory;

import java.io.IOException;

public class ZMQTest extends TestCase {
    @Test
    public void testSimplePushPull() throws Exception {
        Context ctx = ContextFactory.createContext(1);
        try {
            final String expected = "PING";
            Socket puller = ctx.buildSocket(SocketType.PULL).bind("ipc:///tmp/pushpull.ipc");
            Socket pusher = ctx.buildSocket(SocketType.PUSH).connect("ipc:///tmp/pushpull.ipc");
            pusher.send(expected.getBytes());
            byte[] actual = puller.receive();
            assertEquals(expected, new String(actual));
        } finally {
            try {
                ctx.close();
            } catch (IOException ignore) {
            }
        }
    }
}
