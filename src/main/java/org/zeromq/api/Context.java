package org.zeromq.api;

import org.zeromq.jzmq.PollerBuilder;
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
     * @see Pollable
     * @see PollerType
     */
    PollerBuilder buildPoller();

    /**
     * Create a new Pollable from the socket, with the requested options.
     * @param socket
     * @param options
     * @return a new Pollable, for use with a Poller.
     */
    Pollable newPollable(Socket socket, PollerType... options);

    /**
     * Create a ZMQ proxy and start it up.  Returns when the context is closed.
     */
    void proxy(Socket frontEnd, Socket backEnd);
}
