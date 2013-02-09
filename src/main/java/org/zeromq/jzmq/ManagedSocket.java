package org.zeromq.jzmq;

import org.zeromq.ZMQ;
import org.zeromq.api.Context;
import org.zeromq.api.MessageFlag;
import org.zeromq.api.Socket;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Managed JZMQ Socket
 */
public class ManagedSocket implements Socket {
    // private final SocketType socketType;
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
    public void send(byte[] buf) {
        send(buf, 0, MessageFlag.NONE);
    }

    @Override
    public void send(byte[] buf, int offset, MessageFlag flag) {
        socket.send(buf, offset, flag.getFlag());
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
}
