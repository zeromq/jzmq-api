package org.zeromq.api;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ContextFactory;

public class RouterDealerTest extends TestCase {
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
    public void testSimpleRouterDealer() throws Exception {
        Socket router = context.buildSocket(SocketType.ROUTER).bind("ipc://router.ipc");
        Socket dealer1 = context.buildSocket(SocketType.DEALER).withIdentity("A".getBytes()).connect("ipc://router.ipc");
        Socket dealer2 = context.buildSocket(SocketType.DEALER).withIdentity("B".getBytes()).connect("ipc://router.ipc");

        // Unfortunately you have to wait until the dealers connect or dealer1.receive will block. Add poll later
        Thread.sleep(100);
        router.send("A".getBytes(), 0, MessageFlag.SEND_MORE);
        router.send("END".getBytes());
        
        String expected = new String(dealer1.receive());
        assertEquals("END", expected);

        router.send("B".getBytes(), 0, MessageFlag.SEND_MORE);
        router.send("END".getBytes());
        
        expected = new String(dealer2.receive());
        assertEquals("END", expected);
    }
}
