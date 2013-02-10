package org.zeromq.api;

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
}
