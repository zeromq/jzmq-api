package org.zeromq.jzmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.api.*;
import org.zeromq.jzmq.poll.PollableImpl;
import org.zeromq.jzmq.sockets.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manage JZMQ Context
 */
public class ManagedContext implements Context {
    private static final Logger log = LoggerFactory.getLogger(ManagedContext.class);

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final ZMQ.Context context;
    private final Set<Socket> sockets;

    public ManagedContext() {
        this(ZMQ.context(1));
    }

    public ManagedContext(int ioThreads) {
        this(ZMQ.context(ioThreads));
    }

    public ManagedContext(ZMQ.Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.sockets = new CopyOnWriteArraySet<Socket>();
        this.context = context;
    }

    public ZMQ.Context getZMQContext() {
        return context;
    }

    // Do people actually need this?
    public Collection<Socket> getSockets() {
        return Collections.unmodifiableCollection(sockets);
    }

    public void destroySocket(Socket socket) {
        if (sockets.contains(socket)) {
            try {
                socket.getZMQSocket().close();
            } catch (Exception ignore) {
                log.warn("Exception caught while closing underlying socket.", ignore);
            }
            log.info("closed socket");
            sockets.remove(socket);
        }
    }

    @Override
    public void close() throws IOException {
        if (closed.compareAndSet(false, true)) {
            for (Socket s : sockets) {
                destroySocket(s);
            }
            sockets.clear();
            context.term();
            log.info("closed context");
        }
    }

    // I'd really like this to be private but I don't want all the builders in here
    // If we only deal with the Context interface, callers won't see this
    public void addSocket(Socket socket) {
        sockets.add(socket);
    }

    @Override
    public SocketBuilder buildSocket(SocketType type) {
        switch (type) {
            case PULL:
                return new PullSocketBuilder(this);
            case PUSH:
                return new PushSocketBuilder(this);
            case PUB:
                return new PubSocketBuilder(this);
            case SUB:
                return new SubSocketBuilder(this);
            case REP:
                return new RepSocketBuilder(this);
            case REQ:
                return new ReqSocketBuilder(this);
            case ROUTER:
                return new RouterSocketBuilder(this);
            case DEALER:
                return new DealerSocketBuilder(this);
        }
        throw new IllegalArgumentException("Socket type not supported: " + type);
    }

    @Override
    public String getVersionString() {
        return ZMQ.getVersionString();
    }

    @Override
    public int getFullVersion() {
        return ZMQ.getFullVersion();
    }

    @Override
    public PollerBuilder buildPoller() {
        return new PollerBuilder(this);
    }

    public ZMQ.Poller newZmqPoller() {
        return context.poller();
    }

    @Override
    public Pollable newPollable(Socket socket, PollerType... options) {
        return new PollableImpl(socket, options);
    }
}
