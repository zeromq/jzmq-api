package org.zeromq.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;

import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class PollerTest {
    private Context context;

    @Before
    public void setUp() {
        context = new ManagedContext();
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test(timeout = 1000)
    public void testPollIn() throws Exception {
        Socket server = context.buildSocket(SocketType.REP).bind("inproc://repSocket");
        final AtomicBoolean requestReceived = new AtomicBoolean(false);
        Poller testClass = context.buildPoller()
                .withPollable(context.newPollable(server, PollerType.POLL_IN), new PollAdapter() {
                    @Override
                    public void handleIn(Socket socket) {
                        byte[] request = socket.receive();
                        assertArrayEquals("hello".getBytes(), request);
                        requestReceived.set(true);
                    }
                })
                .build();

        Socket request = context.buildSocket(SocketType.REQ).connect("inproc://repSocket");
        request.send("hello".getBytes());

        testClass.poll(100L);
        assertTrue(requestReceived.get());
    }

    @Test(timeout = 1000)
    public void testPollOut() throws Exception {
        Socket server = context.buildSocket(SocketType.REP).bind("inproc://repSocket");

        Socket requester = context.buildSocket(SocketType.REQ).connect("inproc://repSocket");
        Poller testClass = context.buildPoller()
                .withPollable(context.newPollable(requester, PollerType.POLL_OUT), new PollAdapter() {
                    @Override
                    public void handleOut(Socket socket) {
                        socket.send("hello".getBytes());
                    }
                })
                .build();


        testClass.poll(100L);

        byte[] message = server.receive();
        assertArrayEquals("hello".getBytes(), message);
    }

    //todo write a test for the error polling...how do we reliably generate an error on a socket?
}
