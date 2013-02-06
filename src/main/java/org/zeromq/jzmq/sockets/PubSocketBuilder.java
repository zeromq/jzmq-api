package org.zeromq.jzmq.sockets;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class PubSocketBuilder extends SocketBuilder {

    public PubSocketBuilder(ManagedContext context) {
        super(context, SocketType.PUB);
    }

    @Override
    public Socket connect(String url) throws Exception {
        throw new IllegalStateException("Cannot call connect on a PUB Socket type");
    }

    @Override
    public Socket bind(String url) throws Exception {
        // Return me a Pub socket!
        return null;
    }
}
