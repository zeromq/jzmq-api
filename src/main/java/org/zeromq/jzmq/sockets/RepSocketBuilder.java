package org.zeromq.jzmq.sockets;

import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class RepSocketBuilder extends SocketBuilder {
    public RepSocketBuilder(ManagedContext managedContext) {
        super(managedContext, SocketType.REP);
    }

}
