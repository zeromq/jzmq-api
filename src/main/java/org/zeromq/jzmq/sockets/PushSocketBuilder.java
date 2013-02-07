package org.zeromq.jzmq.sockets;

import org.zeromq.ZMQ;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.ManagedSocket;

/**
 * 
 */
public class PushSocketBuilder extends SocketBuilder {

    public PushSocketBuilder(ManagedContext context) {
        super(context, SocketType.PUSH);
    }

    @Override
    public Socket connect(String url) throws Exception {
        ZMQ.Context zmqContext = context.getZMQContext();
        ZMQ.Socket socket = zmqContext.socket(this.getSocketType().getType());
        socket.setLinger(this.getLinger());
        socket.setSndHWM(this.getSendHWM());
        socket.connect(url);
        return new ManagedSocket(context, socket);
    }

    @Override
    public Socket bind(String url) throws Exception {
        throw new IllegalArgumentException("PUSH socket cannot bind");
    }
}
