package org.zeromq.api;

/**
 * A service interface for a server implementing CHP (Clustered Hashmap Protocol).
 */
public interface CloneServer {
    /**
     * Start the underlying Reactor.
     */
    void start();

    /**
     * Stop the underlying Reactor.
     */
    void stop();
}
