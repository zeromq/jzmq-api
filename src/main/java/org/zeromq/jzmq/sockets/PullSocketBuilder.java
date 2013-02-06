package org.zeromq.jzmq.sockets;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class PullSocketBuilder extends SocketBuilder {

    public PullSocketBuilder(ManagedContext context) {
        super(context, SocketType.PULL);
    }

    @Override
    public Socket connect(String url) throws Exception {
        throw new IllegalArgumentException("PULL socket cannot connect");
    }

    @Override
    public Socket bind(String url) throws Exception {
        return null;
    }
}
