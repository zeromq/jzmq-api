package org.zeromq.jzmq.sockets;

import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class ReqSocketBuilder extends SocketBuilder {
    public ReqSocketBuilder(ManagedContext context) {
        super(context, SocketType.REQ);
    }


}
