package org.zeromq.api;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ContextFactory;

import static org.junit.Assert.assertArrayEquals;

public class DealerRepTest extends TestCase {
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

    @Test(timeout = 1000)
    public void testDealerRep() throws Exception {
        Socket dealerSocket = context.buildSocket(SocketType.DEALER).bind("inproc://serverSocket");
        Socket replySocket = context.buildSocket(SocketType.REP).connect("inproc://serverSocket");

        dealerSocket.send("returnAddress".getBytes(), MessageFlag.SEND_MORE);
        dealerSocket.send("".getBytes(), MessageFlag.SEND_MORE);
        dealerSocket.send("request".getBytes());

        byte[] request = replySocket.receive();
        assertArrayEquals("request".getBytes(), request);
        replySocket.send("response".getBytes());

        assertArrayEquals("returnAddress".getBytes(), dealerSocket.receive());
        assertArrayEquals("".getBytes(), dealerSocket.receive());
        assertArrayEquals("response".getBytes(), dealerSocket.receive());
    }

    @Test
    public void testBrokenStuff() throws Exception {
        Socket testServer = context.buildSocket(SocketType.REP).bind("inproc://borken");
        Socket testSocket = context.buildSocket(SocketType.REQ).connect("inproc://borken");
        testSocket.send("".getBytes(), MessageFlag.SEND_MORE);
        testSocket.send("hello, there".getBytes());
        byte[] received = testServer.receive();
        assertEquals(0, received.length);
        testServer.receive();
    }
}
