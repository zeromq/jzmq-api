package org.zeromq.api;

/**
 * An event-driven reactor for discovery beacons.
 */
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
