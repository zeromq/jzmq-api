package org.zeromq.jzmq.sockets;

import org.zeromq.ZMQ;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.ManagedSocket;

public class RouterSocketBuilder extends SocketBuilder {
    public RouterSocketBuilder(ManagedContext managedContext) {
        super(managedContext, SocketType.ROUTER);
    }

    @Override
    public Socket bind(String url, String... additionalUrls) {
        ZMQ.Context zmqContext = context.getZMQContext();
        ZMQ.Socket socket = zmqContext.socket(this.getSocketType().getType());
        socket.setLinger(getLinger());
        socket.setRcvHWM(getReceiveHWM());
        socket.setSndHWM(getSendHWM());
        if (this.getIdentity() != null && this.getIdentity().length > 0) {
            socket.setIdentity(this.getIdentity());
        }
        socket.bind(url);
        for (String s : additionalUrls) {
            socket.bind(s);
        }
        return new ManagedSocket(context, socket);
    }
}
