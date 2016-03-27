package org.zeromq.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;

import java.util.HashSet;
import java.util.Set;

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

        frontend1.send(new Message(1));
        frontend2.send(new Message(2));
        frontend2.send(new Message(3));
        frontend1.send(new Message(4));

        Set<Integer> messages = new HashSet<>();
        messages.add(backend1.receiveMessage().popInt());
        messages.add(backend1.receiveMessage().popInt());
        messages.add(backend2.receiveMessage().popInt());
        messages.add(backend2.receiveMessage().popInt());
        assertEquals(4, messages.size());
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

        frontend1.send(new Message(1));
        frontend2.send(new Message(2));
        frontend2.send(new Message(3));
        frontend2.send(new Message(4));

        assertEquals(1, backend1.receiveMessage().popInt());
        assertEquals(1, backend2.receiveMessage().popInt());
        assertEquals(2, backend1.receiveMessage().popInt());
        assertEquals(2, backend2.receiveMessage().popInt());
        assertEquals(3, backend1.receiveMessage().popInt());
        assertEquals(3, backend2.receiveMessage().popInt());
        assertEquals(4, backend1.receiveMessage().popInt());
        assertEquals(4, backend2.receiveMessage().popInt());
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

        Set<Integer> messages = new HashSet<>();
        frontend1.send(new Message(1));
        frontend2.send(new Message(2));

        messages.add(backend1.receiveMessage().popInt());
        messages.add(backend2.receiveMessage().popInt());

        backend1.send(new Message(3));
        backend2.send(new Message(4));
        messages.add(frontend1.receiveMessage().popInt());
        messages.add(frontend2.receiveMessage().popInt());
        assertEquals(4, messages.size());
    }
}
