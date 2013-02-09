package org.zeromq.api;

import org.zeromq.ZMQ;

import java.io.Closeable;

/**
 * Manage the ZMQ.Socket
 */
public interface Socket extends Sendable, Receiver<byte[]>, Closeable {
    public ZMQ.Socket getZMQSocket();

    public Context getContext();

    public boolean isActive();
}
