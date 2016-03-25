package org.zeromq.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zeromq.Sockets;

public class SocketsTest {
    @Test
    public void testPushPull() {
        try (Socket push = Sockets.bind(SocketType.PUSH, "tcp://*:5991");
             Socket pull = Sockets.connect(SocketType.PULL, "tcp://127.0.0.1:5991")) {
            push.send(new Message("Hello"));
            assertEquals("Hello", pull.receiveMessage().popString());
        }
    }

    @Test
    public void testPubSub() throws Exception {
        try (Socket pub = Sockets.bind(SocketType.PUB, "tcp://*:5992");
             Socket sub = Sockets.buildSocket(SocketType.SUB)
                 .asSubscribable().subscribeAll()
                 .connect("tcp://127.0.0.1:5992")) {
            Thread.sleep(250);
            pub.send(new Message("Hello"));

            assertEquals("Hello", sub.receiveMessage().popString());
        }
    }

    @Test
    public void testReqRepWithPoller() {
        try (Socket req = Sockets.bind(SocketType.REQ, "tcp://*:5993");
             Socket rep = Sockets.connect(SocketType.REP, "tcp://127.0.0.1:5993")) {
            Poller poller = Sockets.newPoller(new PollAdapter(), req, rep);
            req.send(new Message("World"));
            poller.poll();
            rep.send(new Message("Hello, " + rep.receiveMessage().popString()));
            poller.poll();

            assertEquals("Hello, World", req.receiveMessage().popString());
        }
    }
}
