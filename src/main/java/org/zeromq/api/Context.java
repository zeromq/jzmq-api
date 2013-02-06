package org.zeromq.api;

import java.io.Closeable;

import org.zeromq.jzmq.sockets.SocketBuilder;


/**
 * Define a Context interface to encapsulate the ZMQ.Context
 */
public interface Context extends Closeable {
    public SocketBuilder createSocket(SocketType type);
}
