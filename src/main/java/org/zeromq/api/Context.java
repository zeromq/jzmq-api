package org.zeromq.api;

import org.zeromq.jzmq.sockets.SocketBuilder;
import org.zeromq.jzmq.sockets.SubSocketBuilder;

import java.io.Closeable;


/**
 * Define a Context interface to encapsulate the ZMQ.Context
 */
public interface Context extends Closeable {
    SocketBuilder createSocket(SocketType type);
    SubSocketBuilder createSubSocket();
}
