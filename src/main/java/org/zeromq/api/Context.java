package org.zeromq.api;

import org.zeromq.jzmq.sockets.SocketBuilder;
import org.zeromq.jzmq.sockets.SubSocketBuilder;

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
     * Create a SUB socket builder.
     */
    SubSocketBuilder buildSubSocket();
}
