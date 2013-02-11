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
    public void setUp() {
        context = ContextFactory.createContext(1);
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test(timeout = 1000)
    public void testDealerRep() throws Exception {
        Socket dealerSocket = context.buildSocket(SocketType.DEALER).bind("inproc://serverSocket");
        Socket replySocket = context.buildSocket(SocketType.REP).connect("inproc://serverSocket");

        dealerSocket.sendMore("returnAddress");
        dealerSocket.sendMore("");
        dealerSocket.send("request".getBytes());

        byte[] request = replySocket.receive();
        assertArrayEquals("request".getBytes(), request);
        replySocket.send("response".getBytes());

        assertArrayEquals("returnAddress".getBytes(), dealerSocket.receive());
        assertArrayEquals("".getBytes(), dealerSocket.receive());
        assertArrayEquals("response".getBytes(), dealerSocket.receive());
    }
}
