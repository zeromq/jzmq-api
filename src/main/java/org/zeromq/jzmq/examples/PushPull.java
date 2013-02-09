package org.zeromq.jzmq.examples;

import org.zeromq.ContextFactory;
import org.zeromq.api.Context;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;

public class PushPull {
    public static void main(String[] args) throws Exception {
        Context ctx = ContextFactory.createContext(1);
        Socket puller = ctx.buildSocket(SocketType.PULL).bind("ipc:///tmp/pushpull.ipc");
        Socket pusher = ctx.buildSocket(SocketType.PUSH).connect("ipc:///tmp/pushpull.ipc");
        pusher.send("PING".getBytes());
        byte[] buf = puller.receive();
        System.out.println(new String(buf));
        ctx.close();
    }
}