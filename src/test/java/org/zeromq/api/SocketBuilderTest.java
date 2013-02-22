package org.zeromq.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.sockets.SocketBuilder;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class SocketBuilderTest {

    private ManagedContext context;

    @Before
    public void setUp() {
        context = new ManagedContext();
    }

    @After
    public void tearDown() throws Exception{
        context.close();
    }

    @Test
    public void testSendTimeout() throws Exception {
        SocketBuilder testClass = new SocketBuilder(context, SocketType.REQ);
        Socket reqSocket = testClass.withSendTimeout(100).bind("inproc://testSendTimeout");
        boolean result = reqSocket.send("Hello".getBytes());
        assertTrue(!result);
    }

    @Test
    public void testReceiveTimeout() throws Exception {
        SocketBuilder testClass = new SocketBuilder(context, SocketType.REP);
        Socket repSocket = testClass.withReceiveTimeout(100).bind("inproc://testReceiveTimeout");
        byte[] result = repSocket.receive();
        assertNull(result);
    }


}
