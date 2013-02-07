package org.zeromq.api;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;
import org.zeromq.ContextFactory;

public class ZMQTest extends TestCase {
    @Test
    public void testSimplePushPull() throws Exception {
        Context ctx = ContextFactory.createContext(1);
        try {
            final String expected = "PING";
            Socket puller = ctx.createSocket(SocketType.PULL).bind("ipc:///tmp/pushpull.ipc");
            Socket pusher = ctx.createSocket(SocketType.PUSH).connect("ipc:///tmp/pushpull.ipc");
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
