package org.zeromq.jzmq.examples;

import org.zeromq.ContextFactory;
import org.zeromq.api.Context;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;

public class PushPull {
    public static void main(String[] args) throws Exception {
        Context ctx = ContextFactory.createContext(1);
        Socket puller = ctx.buildSocket(SocketType.PULL).bind("inproc://pipeline");
        Socket pusher = ctx.buildSocket(SocketType.PUSH).connect("inproc://pipeline");
        pusher.send("PING".getBytes());
        byte[] buf = puller.receive();
        System.out.println(new String(buf));
        ctx.close();
    }
}