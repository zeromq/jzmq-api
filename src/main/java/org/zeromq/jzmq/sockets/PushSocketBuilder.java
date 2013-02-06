package org.zeromq.jzmq.sockets;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

/**
 * 
 */
public class PushSocketBuilder extends SocketBuilder {

    public PushSocketBuilder(ManagedContext context) {
        super(context, SocketType.PUSH);
    }

    @Override
    public Socket connect(String url) throws Exception {
        return null;
    }

    @Override
    public Socket bind(String url) throws Exception {
        throw new IllegalArgumentException("PUSH socket cannot bind");
    }
}
