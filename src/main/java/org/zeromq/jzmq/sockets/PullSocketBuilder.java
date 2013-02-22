package org.zeromq.jzmq.sockets;

import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class PullSocketBuilder extends SocketBuilder {

    public PullSocketBuilder(ManagedContext context) {
        super(context, SocketType.PULL);
    }

}
