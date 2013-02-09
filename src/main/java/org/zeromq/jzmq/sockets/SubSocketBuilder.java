package org.zeromq.jzmq.sockets;

import org.zeromq.ZMQ;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.api.Subscribable;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.ManagedSocket;

public class SubSocketBuilder extends SocketBuilder implements Subscribable {
    private byte[] subscription;

    public SubSocketBuilder(ManagedContext context) {
        super(context, SocketType.SUB);
    }

    @Override
    public SocketBuilder subscribe(byte[] data) {
        subscription = new byte[data.length];
        System.arraycopy(data, 0, subscription, 0, data.length);
        return this;
    }

    @Override
    public Socket connect(String url) throws Exception {
        ZMQ.Context zmqContext = context.getZMQContext();
        ZMQ.Socket socket = zmqContext.socket(this.getSocketType().getType());
        socket.setLinger(this.getLinger());
        socket.setRcvHWM(this.getRecvHWM());
        socket.subscribe(subscription);

        socket.connect(url);
        return new ManagedSocket(context, socket);
    }

    @Override
    public Socket bind(String url) throws Exception {
        return null;
    }
}
