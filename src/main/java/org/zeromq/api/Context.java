package org.zeromq.api;

import org.zeromq.jzmq.poll.PollerBuilder;
import org.zeromq.jzmq.sockets.SocketBuilder;

import java.io.Closeable;

/**
 * A ØMQ context is thread safe and may be shared among as many application threads as necessary, without any additional
 * locking required on the part of the caller.
 */
public interface Context extends Closeable {
    /**
     * Create a ØMQ Socket of type SocketType
     * 
     * @param type socket type
     * @return builder object
     */
    SocketBuilder buildSocket(SocketType type);

    /**
     * @return The ØMQ version, in a pretty-printed String.
     */
    String getVersionString();

    /**
     * @return The ØMQ version, in integer form.
     */
    int getFullVersion();

    /**
     * Create a new Poller, which will allow callback-based polling of Sockets.
     * 
     * @see Pollable
     * @see PollerType
     */
    PollerBuilder buildPoller();

    /**
     * Create a new Pollable from the socket, with the requested options.
     * 
     * @param socket A socket to wrap for polling
     * @param options Polling options (IN, OUT, ERROR)
     * @return A new Pollable, for use with a Poller.
     */
    Pollable newPollable(Socket socket, PollerType... options);

    /**
     * Create a ZMQ proxy and start it up.  Returns when the context is closed.
     * 
     * @param frontEnd The front-end socket which will be proxied to/from the back-end
     * @param backEnd The back-end socket which will be proxied to/from the front-end
     */
    void proxy(Socket frontEnd, Socket backEnd);

    /**
     * Create a ZMQ proxy and start it up on another thread that exits when the context
     * is closed.
     *
     * @param frontEnd The front-end socket which will be proxied to/from the back-end
     * @param backEnd The back-end socket which will be proxied to/from the front-end
     */
    void forward(Socket frontEnd, Socket backEnd);

    /**
     * Alias of {@link #forward}.
     *
     * @param frontEnd The front-end socket which will be proxied to/from the back-end
     * @param backEnd The back-end socket which will be proxied to/from the front-end
     */
    void queue(Socket frontEnd, Socket backEnd);

    /**
     * Run a background thread with an inproc PAIR socket for communication.
     * 
     * @param backgroundable The task to be performed on the background thread
     * @param args Optional arguments for the task
     * @return The inproc PAIR socket for communicating with the background thread
     */
    Socket fork(Backgroundable backgroundable, Object... args);

    /**
     * Run a background thread using the given socket for communication.
     * 
     * @param socket The socket owned by the background thread
     * @param backgroundable The task to be performed on the background thread
     * @param args Optional arguments for the task
     */
    void fork(Socket socket, Backgroundable backgroundable, Object... args);

    /**
     * Create a new Context with the same underlying ØMQ context, with an empty
     * (separate) list of managed Sockets and Backgroundables.
     * <p>
     * The returned context will not attempt to terminate the underlying ØMQ
     * context when closed, and will only close managed sockets, etc.
     * 
     * @return A new context with the same underlying ØMQ context
     */
    Context shadow();

    /**
     * Close the context and any open sockets.
     */
    void close();

    /**
     * Asynchronously terminate the context without closing any open sockets,
     * forcing pollers and waiters to abort.
     */
    void terminate();
}
