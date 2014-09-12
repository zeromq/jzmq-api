package org.zeromq.jzmq;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zeromq.ZMQ;
import org.zeromq.api.Context;
import org.zeromq.api.Message;
import org.zeromq.api.Message.Frame;
import org.zeromq.api.MessageFlag;
import org.zeromq.api.RoutedMessage;
import org.zeromq.api.Socket;
import org.zeromq.api.TransportType;

/**
 * Managed JZMQ Socket
 */
public class ManagedSocket implements Socket {
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private ManagedContext managedContext;
    private ZMQ.Socket socket;

    public ManagedSocket(ManagedContext managedContext, ZMQ.Socket socket) {
        this.socket = socket;
        this.managedContext = managedContext;
        this.managedContext.addSocket(this);
    }

    @Override
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
    public Message receiveMessage() {
        return fillInFrames(new Message());
    }

    @Override
    public RoutedMessage receiveRoutedMessage() {
        return fillInFrames(new RoutedMessage());
    }

    private <T extends Message> T fillInFrames(T result) {
        byte[] bytes = receive();
        if (bytes == null) {
            return null;
        }
        result.addFrame(new Frame(bytes));
        while (hasMoreToReceive()) {
            byte[] data = receive();
            result.addFrame(new Frame(data));
        }
        return result;
    }

    @Override
    public boolean send(Message message) {
        int frameNumber = 0;
        for (Frame frame : message) {
            if (++frameNumber < message.size()) {
                boolean sent = send(frame.getData(), MessageFlag.SEND_MORE);
                if (!sent) {
                    return false;
                }
            } else {
                return send(frame.getData());
            }
        }
        return true;  // no frames? What should we return?
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
    public void close() {
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
