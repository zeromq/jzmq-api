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
        System.out.println("beginning of test");
        Socket dealerSocket = context.buildSocket(SocketType.DEALER).bind("inproc://serverSocket");
        System.out.println("built dealer socket");
        Socket replySocket = context.buildSocket(SocketType.REP).connect("inproc://serverSocket");
        System.out.println("built REP socket");

        dealerSocket.send("returnAddress".getBytes(), MessageFlag.SEND_MORE);
        System.out.println("sent address");
        dealerSocket.send("".getBytes(), MessageFlag.SEND_MORE);
        System.out.println("sent blank");
        dealerSocket.send("request".getBytes());
        System.out.println("sent request frame");

        byte[] request = replySocket.receive();
        System.out.println("request received");
        assertArrayEquals("request".getBytes(), request);
        System.out.println("asserted array equal");
        replySocket.send("response".getBytes());
        System.out.println("sent response");

        assertArrayEquals("returnAddress".getBytes(), dealerSocket.receive());
        System.out.println("asserted return address");
        assertArrayEquals("".getBytes(), dealerSocket.receive());
        System.out.println("asserted blank");
        assertArrayEquals("response".getBytes(), dealerSocket.receive());
        System.out.println("asserted response");
    }
}
