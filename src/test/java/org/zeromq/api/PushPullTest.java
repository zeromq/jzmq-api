package org.zeromq.api;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ContextFactory;

public class PushPullTest extends TestCase {
    private Context context;

    @Before
    @Override
    public void setUp() {
        context = ContextFactory.createContext(1);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void testSimplePushPull() throws Exception {
        String expected = "PING";
        Socket puller = context.buildSocket(SocketType.PULL).bind("ipc://pushpull.ipc");
        Socket pusher = context.buildSocket(SocketType.PUSH).connect("ipc://pushpull.ipc");
        pusher.send(expected.getBytes());
        byte[] actual = puller.receive();
        assertEquals(expected, new String(actual));
    }
}
