package org.zeromq.jzmq.sockets;

import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class PubSocketBuilder extends SocketBuilder {

    public PubSocketBuilder(ManagedContext context) {
        super(context, SocketType.PUB);
    }

}
