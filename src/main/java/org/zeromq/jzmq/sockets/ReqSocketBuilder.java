package org.zeromq.jzmq.sockets;

import org.zeromq.ZMQ;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.ManagedSocket;

public class ReqSocketBuilder extends SocketBuilder {
    public ReqSocketBuilder(ManagedContext context) {
        super(context, SocketType.REQ);
    }

    @Override
    public Socket connect(String url) {
        ZMQ.Socket socket = createConnectableSocketWithStandardSettings();
        socket.connect(url);
        return new ManagedSocket(context, socket);
    }

}
