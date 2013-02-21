package org.zeromq.jzmq;

import org.zeromq.ZMQ;
import org.zeromq.api.Context;
import org.zeromq.api.MessageFlag;
import org.zeromq.api.Socket;
import org.zeromq.api.TransportType;

import java.io.IOException;
import java.nio.ByteBuffer;
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
    public int receive(byte[] buf, int offset, int len, MessageFlag flag) {
        return socket.recv(buf, offset, len, flag.getFlag());
    }

    @Override
    public int receiveZeroCopy(ByteBuffer buf, int len, MessageFlag flag) {
        return socket.recvZeroCopy(buf, len, flag.getFlag());
    }

    @Override
    public boolean hasMoreToReceive() {
        return socket.hasReceiveMore();
    }

    @Override
    public boolean send(byte[] buf) {
        return send(buf, 0, buf.length, MessageFlag.NONE);
    }

    @Override
    public boolean send(byte[] message, MessageFlag flag) {
        return send(message, 0, message.length, flag);
    }

    @Override
    public boolean send(byte[] buf, int offset, int length, MessageFlag flag) {
        return socket.send(buf, offset, length, flag.getFlag());
    }

    @Override
    public boolean sendZeroCopy(ByteBuffer buf, int length, MessageFlag flag) {
        return socket.sendZeroCopy(buf, length, flag.getFlag());
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
