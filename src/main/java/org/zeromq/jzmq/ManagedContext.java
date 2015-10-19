package org.zeromq.jzmq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.api.Backgroundable;
import org.zeromq.api.Context;
import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.api.exception.ZMQExceptions;
import org.zeromq.jzmq.poll.PollableImpl;
import org.zeromq.jzmq.poll.PollerBuilder;
import org.zeromq.jzmq.sockets.DealerSocketBuilder;
import org.zeromq.jzmq.sockets.PairSocketBuilder;
import org.zeromq.jzmq.sockets.PubSocketBuilder;
import org.zeromq.jzmq.sockets.PullSocketBuilder;
import org.zeromq.jzmq.sockets.PushSocketBuilder;
import org.zeromq.jzmq.sockets.RepSocketBuilder;
import org.zeromq.jzmq.sockets.ReqSocketBuilder;
import org.zeromq.jzmq.sockets.RouterSocketBuilder;
import org.zeromq.jzmq.sockets.SocketBuilder;
import org.zeromq.jzmq.sockets.SubSocketBuilder;

/**
 * Manage JZMQ Context
 */
public class ManagedContext implements Context {
    private static final Logger log = LoggerFactory.getLogger(ManagedContext.class);

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final boolean termContext;
    private final ZMQ.Context context;
    private final Set<Socket> sockets;
    private final List<Backgroundable> backgroundables;

    public ManagedContext() {
        this(ZMQ.context(1));
    }

    public ManagedContext(int ioThreads) {
        this(ZMQ.context(ioThreads));
    }

    public ManagedContext(ZMQ.Context context) {
        this(context, true);
    }

    public ManagedContext(ZMQ.Context context, boolean termContext) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.sockets = new CopyOnWriteArraySet<>();
        this.backgroundables = new ArrayList<>();
        this.context = context;
        this.termContext = termContext;
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
            log.debug("closed socket");
            sockets.remove(socket);
        }
    }

    @Override
    public ManagedContext shadow() {
        return new ManagedContext(context, false);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            for (Backgroundable b : backgroundables) {
                b.onClose();
            }
            for (Socket s : sockets) {
                destroySocket(s);
            }
            sockets.clear();
            if (termContext) {
                context.term();
            }
            log.debug("closed context");
        }
    }

    // I'd really like this to be private but I don't want all the builders in here
    // If we only deal with the Context interface, callers won't see this
    void addSocket(Socket socket) {
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
            case PAIR:
                return new PairSocketBuilder(this);
            default:
                throw new IllegalArgumentException("Socket type not supported: " + type);
        }
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

    public ZMQ.Poller newZmqPoller(int initialNumberOfItems) {
        return new ZMQ.Poller(initialNumberOfItems);
    }

    public ZMQ.Poller newZmqPoller() {
        return newZmqPoller(32);
    }

    @Override
    public Pollable newPollable(Socket socket, PollerType... options) {
        return new PollableImpl(socket, options);
    }

    @Override
    public void proxy(Socket frontEnd, Socket backEnd) {
        ZMQ.proxy(frontEnd.getZMQSocket(), backEnd.getZMQSocket(), null);
    }

    @Override
    public void forward(Socket frontEnd, Socket backEnd) {
        new ProxyThread(this, frontEnd, backEnd).start();
    }

    @Override
    public void queue(Socket frontEnd, Socket backEnd) {
        forward(frontEnd, backEnd);
    }

    public void addBackgroundable(Backgroundable backgroundable) {
        backgroundables.add(backgroundable);
    }

    @Override
    public Socket fork(Backgroundable backgroundable, Object... args) {
        int pipeId = backgroundable.hashCode();
        String endpoint = String.format("inproc://jzmq-pipe-%d", pipeId);
        
        // link PAIR pipes together
        Socket frontend = buildSocket(SocketType.PAIR).bind(endpoint);
        Socket backend = buildSocket(SocketType.PAIR).connect(endpoint);
        
        // start child thread
        fork(backend, backgroundable, args);
        
        return frontend;
    }

    @Override
    public void fork(Socket socket, Backgroundable backgroundable, Object... args) {
        Thread shim = new ShimThread(this, backgroundable, socket, args);
        addBackgroundable(backgroundable);
        shim.start();
    }

    /**
     * Internal worker class for forking inproc PAIR sockets.
     * @see org.zeromq.ZThread
     */
    private static class ShimThread extends Thread {
        private ManagedContext context;
        private Backgroundable backgroundable;
        private Socket pipe;
        private Object[] args;
        
        public ShimThread(ManagedContext context, Backgroundable backgroundable, Socket pipe, Object... args) {
            this.context = context;
            this.backgroundable = backgroundable;
            this.pipe = pipe;
            this.args = args;
        }
        
        @Override
        public void run() {
            try {
                backgroundable.run(context, pipe, args);
            } catch (ZMQException ex) {
                if (!ZMQExceptions.isContextTerminated(ex)) {
                    throw ZMQExceptions.wrap(ex);
                }
            }
            context.close();
            log.debug("Background thread {} has shut down", Thread.currentThread().getName());
        }
    }

    private static class ProxyThread extends Thread {
        private ManagedContext context;
        private Socket frontEnd;
        private Socket backEnd;

        public ProxyThread(ManagedContext context, Socket frontEnd, Socket backEnd) {
            this.context = context;
            this.frontEnd = frontEnd;
            this.backEnd = backEnd;
        }

        @Override
        public void run() {
            try {
                context.proxy(frontEnd, backEnd);
            } catch (ZMQException ex) {
                if (!ZMQExceptions.isContextTerminated(ex)) {
                    throw ZMQExceptions.wrap(ex);
                }
            }
            log.debug("Proxy thread {} has shut down", Thread.currentThread().getName());
        }
    }

}
