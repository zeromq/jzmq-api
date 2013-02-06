package org.zeromq.api;

import java.io.Closeable;

import org.zeromq.ZMQ;

/**
 * Manage the ZMQ.Socket
 */
public interface Socket extends Sendable, Receivable<byte[]>, Closeable {
    public ZMQ.Socket getZMQSocket();

    public Context getContext();

    public boolean isActive();
}
