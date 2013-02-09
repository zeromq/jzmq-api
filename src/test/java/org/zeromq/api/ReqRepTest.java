package org.zeromq.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;

import static org.junit.Assert.assertArrayEquals;

public class ReqRepTest {

    private ManagedContext context;

    @Before
    public void setUp() {
        context = new ManagedContext();
    }

    @After
    public void tearDown() throws Exception{
        context.close();
    }

    @Test(timeout = 1000)
    public void testReqRep() throws Exception {
        final Socket repSocket = context.buildSocket(SocketType.REP).bind("inproc://serverSocket");
        Socket requestSocket = context.buildSocket(SocketType.REQ).connect("inproc://serverSocket");
        requestSocket.send("request".getBytes());
        byte[] request = repSocket.receive();
        assertArrayEquals("request".getBytes(), request);
        repSocket.send("response".getBytes());
        byte[] response = requestSocket.receive();
        assertArrayEquals("response".getBytes(), response);
    }
}
