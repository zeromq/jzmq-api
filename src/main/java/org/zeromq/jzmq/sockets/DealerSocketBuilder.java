package org.zeromq.jzmq.sockets;

import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class DealerSocketBuilder extends SocketBuilder {
    public DealerSocketBuilder(ManagedContext context) {
        super(context, SocketType.DEALER);
    }

}
