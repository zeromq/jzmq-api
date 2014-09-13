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
     * @return the ØMQ version, in a pretty-printed String.
     */
    String getVersionString();

    /**
     * @return the ØMQ version, in integer form.
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
     * @return a new Pollable, for use with a Poller.
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
     * Run a background thread with an inproc PAIR socket for communication.
     * 
     * @param backgroundable The task to be performed on the background thread
     * @param args Optional arguments for the task
     * @return the inproc PAIR socket for communicating with the background thread
     */
    Socket fork(Backgroundable backgroundable, Object... args);

    /**
     * Close the context and any open sockets.
     */
    void close();
}
