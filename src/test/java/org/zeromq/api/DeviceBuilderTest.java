package org.zeromq.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;

public class DeviceBuilderTest {
    private ManagedContext context;

    @Before
    public void setUp() {
        context = new ManagedContext();
    }
    
    @After
    public void tearDown() throws Exception {
        context.terminate();
        context.close();
    }
    
    @Test
    public void testStreamer() throws Exception {
        context.buildDevice(DeviceType.STREAMER)
            .withFrontendUrl("inproc://streamer-frontend")
            .withBackendUrl("inproc://streamer-backend")
            .start();

        Socket frontend1 = context.buildSocket(SocketType.PUSH)
            .connect("inproc://streamer-frontend");
        Socket frontend2 = context.buildSocket(SocketType.PUSH)
            .connect("inproc://streamer-frontend");
        Socket backend1 = context.buildSocket(SocketType.PULL)
            .connect("inproc://streamer-backend");
        Socket backend2 = context.buildSocket(SocketType.PULL)
            .connect("inproc://streamer-backend");

        ZInteger buf = new ZInteger();
        buf.put(1).send(frontend1);
        buf.put(2).send(frontend2);
        buf.put(3).send(frontend2);
        buf.put(4).send(frontend1);

        assertEquals(1, buf.receive(backend1));
        assertEquals(2, buf.receive(backend2));
        assertEquals(3, buf.receive(backend2));
        assertEquals(4, buf.receive(backend1));
    }

    @Test
    public void testForwarder() throws Exception {
        context.buildDevice(DeviceType.FORWARDER)
            .withFrontendUrl("inproc://forwarder-frontend")
            .withBackendUrl("inproc://forwarder-backend")
            .start();

        Socket frontend1 = context.buildSocket(SocketType.PUB)
            .connect("inproc://forwarder-frontend");
        Socket frontend2 = context.buildSocket(SocketType.PUB)
            .connect("inproc://forwarder-frontend");

        Socket backend1 = context.buildSocket(SocketType.SUB)
            .asSubscribable().subscribeAll()
            .connect("inproc://forwarder-backend");
        Socket backend2 = context.buildSocket(SocketType.SUB)
            .asSubscribable().subscribeAll()
            .connect("inproc://forwarder-backend");

        // Give slow joiner some time
        Thread.sleep(25);

        ZInteger buf = new ZInteger();
        buf.put(1).send(frontend1);
        buf.put(2).send(frontend2);
        buf.put(3).send(frontend2);
        buf.put(4).send(frontend2);

        assertEquals(1, buf.receive(backend1));
        assertEquals(1, buf.receive(backend2));
        assertEquals(2, buf.receive(backend1));
        assertEquals(2, buf.receive(backend2));
        assertEquals(3, buf.receive(backend1));
        assertEquals(3, buf.receive(backend2));
        assertEquals(4, buf.receive(backend1));
        assertEquals(4, buf.receive(backend2));
    }

    @Test
    public void testQueue() throws Exception {
        context.buildDevice(DeviceType.QUEUE)
            .withFrontendUrl("inproc://queue-frontend")
            .withBackendUrl("inproc://queue-backend")
            .start();

        Socket frontend1 = context.buildSocket(SocketType.REQ)
            .connect("inproc://queue-frontend");
        Socket frontend2 = context.buildSocket(SocketType.REQ)
            .connect("inproc://queue-frontend");

        Socket backend1 = context.buildSocket(SocketType.REP)
            .connect("inproc://queue-backend");
        Socket backend2 = context.buildSocket(SocketType.REP)
            .connect("inproc://queue-backend");

        ZInteger buf = new ZInteger();
        buf.put(1).send(frontend1);
        buf.put(2).send(frontend2);
        assertEquals(1, buf.receive(backend1));
        assertEquals(2, buf.receive(backend2));
        buf.put(3).send(backend1);
        buf.put(4).send(backend2);
        assertEquals(3, buf.receive(frontend1));
        assertEquals(4, buf.receive(frontend2));

        buf.put(5).send(frontend1);
        buf.put(6).send(frontend2);
        assertEquals(5, buf.receive(backend1));
        assertEquals(6, buf.receive(backend2));
        buf.put(7).send(backend1);
        buf.put(8).send(backend2);
        assertEquals(7, buf.receive(frontend1));
        assertEquals(8, buf.receive(frontend2));
    }
}
