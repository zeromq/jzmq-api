package org.zeromq.jzmq;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.api.Context;
import org.zeromq.api.Message;
import org.zeromq.api.Message.Frame;
import org.zeromq.api.MessageFlag;
import org.zeromq.api.RoutedMessage;
import org.zeromq.api.Socket;
import org.zeromq.api.TransportType;
import org.zeromq.api.exception.ContextTerminatedException;
import org.zeromq.api.exception.InvalidSocketException;
import org.zeromq.api.exception.ZMQExceptions;

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
        try {
            return socket.recv(0);
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }
    }

    @Override
    public byte[] receive(MessageFlag flag) {
        try {
            return socket.recv(flag.getFlag());
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }
    }

    @Override
    public int receive(byte[] buf, int offset, int len, MessageFlag flag) {
        try {
            return socket.recv(buf, offset, len, flag.getFlag());
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }
    }

    @Override
    public int receiveByteBuffer(ByteBuffer buf, MessageFlag flag) {
        try {
            return socket.recvByteBuffer(buf, flag.getFlag());
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }
    }

    @Override
    public boolean hasMoreToReceive() {
        try {
            return socket.hasReceiveMore();
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }
    }


    @Override
    public Message receiveMessage() {
        Message message = null;
        try {
            message = fillInFrames(new Message());
        } catch (ContextTerminatedException ex) {
        } catch (InvalidSocketException ex) {
        }
        return message;
    }

    @Override
    public RoutedMessage receiveRoutedMessage() {
        RoutedMessage message = null;
        try {
            message = fillInFrames(new RoutedMessage());
        } catch (ContextTerminatedException ex) {
        } catch (InvalidSocketException ex) {
        }
        return message;
    }

    private <T extends Message> T fillInFrames(T message) {
        byte[] bytes = receive();
        if (bytes == null) {
            return null;
        }
        message.addFrame(new Frame(bytes));
        while (hasMoreToReceive()) {
            byte[] data = receive();
            message.addFrame(new Frame(data));
        }
        return message;
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
        try {
            return socket.send(buf, offset, length, flag.getFlag());
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }
    }

    @Override
    public boolean sendByteBuffer(ByteBuffer buf, MessageFlag flag) {
        try {
            return socket.sendByteBuffer(buf, flag.getFlag()) >= 0;
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }
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
