package org.zeromq.jzmq.sockets;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.api.Subscribable;
import org.zeromq.jzmq.ManagedContext;

public class SubSocketBuilder extends SocketBuilder implements Subscribable {
    private byte[] subscription;

    public SubSocketBuilder(ManagedContext context) {
        super(context, SocketType.SUB);
    }

    @Override
    public SocketBuilder subscrible(byte[] data) {
        subscription = new byte[data.length];
        System.arraycopy(data, 0, subscription, 0, data.length);
        return this;
    }

    @Override
    public Socket connect(String url) throws Exception {
        return null;
    }

    @Override
    public Socket bind(String url) throws Exception {
        return null;
    }
}
