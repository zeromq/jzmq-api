package org.zeromq.jzmq.sockets;

import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class PairSocketBuilder extends SocketBuilder {

    public PairSocketBuilder(ManagedContext context) {
        super(context, SocketType.PAIR);
    }

}
