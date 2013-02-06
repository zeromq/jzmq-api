package org.zeromq.jzmq.examples;

import org.zeromq.api.Context;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class PullTest {
    public static void main(String[] args) throws Exception {
        Context ctx = new ManagedContext(1); // This should be some sort of factory to avoid using concrete classes
        Socket puller = ctx.createSocket(SocketType.PULL).bind("ipc:///tmp/pushpull.ipc");
        Socket pusher = ctx.createSocket(SocketType.PUSH).connect("ipc:///tmp/pushpull.ipc");

        pusher.send("PING".getBytes());
        byte[] buf = puller.receive();
        System.out.println(new String(buf));

        pusher.close();
        puller.close();
        ctx.close();
    }
}
