package org.zeromq.jzmq.sockets;

import org.zeromq.ZMQ;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.ManagedSocket;

public class PubSocketBuilder extends SocketBuilder {

    public PubSocketBuilder(ManagedContext context) {
        super(context, SocketType.PUB);
    }

    @Override
    public Socket connect(String url) throws Exception {
        throw new IllegalStateException("Cannot call connect on a PUB Socket type");
    }

    @Override
    public Socket bind(String url) throws Exception {
        ZMQ.Context zmqContext = context.getZMQContext();
        ZMQ.Socket socket = zmqContext.socket(this.getSocketType().getType());
        socket.setLinger(this.getLinger());
        socket.setRcvHWM(this.getRecvHWM());
        socket.bind(url);
        return new ManagedSocket(context, socket);
    }
}
