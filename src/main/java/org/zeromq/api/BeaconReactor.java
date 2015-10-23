package org.zeromq.api;

public interface BeaconReactor {
    /**
     * Start the underlying Reactor.
     */
    void start();

    /**
     * Stop the underlying Reactor.
     */
    void stop();
}
