package org.zeromq.jzmq.examples;

import org.zeromq.ContextFactory;
import org.zeromq.api.Context;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;

public class RequestReply {
    public static void main(String[] args) throws Exception {
        Context ctx = ContextFactory.createContext(1);
        Socket rep = ctx.buildSocket(SocketType.REP).bind("inproc://requestreply");
        Socket req = ctx.buildSocket(SocketType.REQ).connect("inproc://requestreply");
        req.send("PING".getBytes());
        byte[] buf = rep.receive();
        System.out.println(new String(buf));
        rep.send("PONG".getBytes());
        buf = req.receive();
        System.out.println(new String(buf));
        ctx.close();
    }
}
