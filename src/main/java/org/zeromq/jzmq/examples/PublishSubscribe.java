package org.zeromq.jzmq.examples;

import org.zeromq.ContextFactory;
import org.zeromq.api.Context;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;

public class PublishSubscribe {
    public static void main(String[] args) throws Exception {
        Context ctx = ContextFactory.createContext(1);
        Socket publisher = ctx.buildSocket(SocketType.PUB).bind("inproc://publisher");
        Socket subscriber = ctx.buildSocket(SocketType.SUB).asSubscribable().subscribe("H".getBytes()).connect("inproc://publisher");
        publisher.send("Hello".getBytes());
        System.out.println(new String(subscriber.receive()));
        ctx.close();
    }
}
