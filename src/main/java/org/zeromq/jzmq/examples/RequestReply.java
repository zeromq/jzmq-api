package org.zeromq.jzmq.examples;

import org.zeromq.api.Context;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class RequestReply {
    public static void main(String[] args) throws Exception {
        Context ctx = new ManagedContext(1); // This should be some sort of factory to avoid using concrete classes
        Socket rep = ctx.createSocket(SocketType.REP).bind("ipc:///tmp/pushpull.ipc");
        Socket req = ctx.createSocket(SocketType.REQ).connect("ipc:///tmp/pushpull.ipc");

        req.send("PING".getBytes());
        byte[] buf = rep.receive();
        System.out.println(new String(buf));
        rep.send("PONG".getBytes());
        buf = req.receive();
        System.out.println(new String(buf));

        req.close();
        rep.close();
        ctx.close();
    }
}
