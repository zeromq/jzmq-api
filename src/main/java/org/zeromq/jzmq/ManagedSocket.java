package org.zeromq.jzmq;

import org.zeromq.ZMQ;
import org.zeromq.api.Context;
import org.zeromq.api.MessageFlag;
import org.zeromq.api.Socket;
import org.zeromq.api.TransportType;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Managed JZMQ Socket
 */
public class ManagedSocket implements Socket {
    // private final SocketType socketType;
    // private final TransportType transportType;
    // private final long lingerMS;
    // private final long sendHWM;
    // private final long recvHWM;

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private ManagedContext managedContext;
    private ZMQ.Socket socket;

    public ManagedSocket(ManagedContext managedContext, ZMQ.Socket socket) {
        this.socket = socket;
        this.managedContext = managedContext;
        this.managedContext.addSocket(this);
    }

    public ZMQ.Socket getZMQSocket() {
        return socket;
    }

    @Override
    public boolean isActive() {
        return !isClosed.get();
    }

    @Override
    public byte[] receive() {
        return socket.recv(0);
    }

    @Override
    public byte[] receive(MessageFlag flag) {
        return socket.recv(flag.getFlag());
    }

    @Override
    public boolean hasMoreToReceive() {
        return socket.hasReceiveMore();
    }

    @Override
    public boolean send(byte[] buf) {
        return send(buf, 0, MessageFlag.NONE);
    }

    @Override
    public boolean send(byte[] buf, MessageFlag flag) {
        return socket.send(buf, flag.getFlag());
    }

    @Override
    public boolean send(byte[] buf, int offset, MessageFlag flag) {
        return socket.send(buf, offset, flag.getFlag());
    }

    @Override
    public void close() throws IOException {
        if (isClosed.compareAndSet(false, true)) {
            managedContext.destroySocket(this);
        }
    }

    @Override
    public Context getContext() {
        return managedContext;
    }

    @Override
    public TransportType getTransportType() {
        return null;
    }
}
