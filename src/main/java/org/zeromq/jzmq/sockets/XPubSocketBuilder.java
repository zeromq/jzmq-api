package org.zeromq.jzmq.sockets;

import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class XPubSocketBuilder extends SocketBuilder {

    public XPubSocketBuilder(ManagedContext context) {
        super(context, SocketType.XPUB);
    }

}
