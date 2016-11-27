package org.zeromq.api;

import java.io.Closeable;

import org.zeromq.ZMQ;

/**
 * ØMQ sockets provide an abstraction of asynchronous message queues, multiple messaging patterns, message filtering
 * (subscriptions), seamless access to multiple transport protocols and more.
 */
public interface Socket extends Sender, Receiver, Closeable {
    // This is JZMQ specific. Eventually need to abstract this away.
    ZMQ.Socket getZMQSocket();

    /**
     * Retrieve the owning Context for this Socket.
     * 
     * @return The ØMQ Context
     */
    Context getContext();

    /**
     * Retrieve the status of this ØMQ Socket.
     * 
     * @return true if the Socket is open, false otherwise
     */
    boolean isActive();

    /**
     * Retrieve the type of transport this ØMQ Socket is using. UNIMPLEMENTED!
     * 
     * @return The transport type used by this Socket
     */
    TransportType getTransportType();

    /**
     * Close this ØMQ Socket.
     */
    void close();
}
