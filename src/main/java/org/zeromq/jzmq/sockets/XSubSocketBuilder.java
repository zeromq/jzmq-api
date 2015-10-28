package org.zeromq.jzmq.sockets;

import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class XSubSocketBuilder extends SocketBuilder {

    public XSubSocketBuilder(ManagedContext context) {
        super(context, SocketType.XSUB);
    }

}
