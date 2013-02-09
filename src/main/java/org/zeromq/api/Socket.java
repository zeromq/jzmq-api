package org.zeromq.api;

import java.io.Closeable;

import org.zeromq.ZMQ;

/**
 * ØMQ sockets provide an abstraction of asynchronous message queues, multiple messaging patterns, message filtering
 * (subscriptions), seamless access to multiple transport protocols and more.
 */
public interface Socket extends Sender, Receiver, Closeable {
    // This is JZMQ specific. Eventually need to abstract this away.
    public ZMQ.Socket getZMQSocket();

    public Context getContext();

    public boolean isActive();

    public TransportType getTransportType();
}
