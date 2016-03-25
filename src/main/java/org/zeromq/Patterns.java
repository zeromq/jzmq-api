package org.zeromq;

import org.zeromq.jzmq.beacon.BeaconReactorBuilder;
import org.zeromq.jzmq.bstar.BinaryStarReactorBuilder;
import org.zeromq.jzmq.bstar.BinaryStarSocketBuilder;

/**
 * Class containing utility methods for creating objects for different patterns
 * without using a ØMQ Context directly.
 */
public class Patterns {
    /**
     * Protected constructor.
     */
    private Patterns() {
    }

    /**
     * Create a new BinaryStarReactor, which will create one half of an HA-pair
     * with event-driven polling of a client Socket.
     * 
     * @return A builder for constructing a BinaryStarReactor
     */
    public static BinaryStarReactorBuilder buildBinaryStarReactor() {
        return ContextFactory.context().buildBinaryStarReactor();
    }

    /**
     * Create a ØMQ Socket, backed by a background agent that is connecting
     * to a BinaryStarReactor HA-pair.
     * 
     * @return A builder for constructing connecting a BinaryStarReactor client Socket
     */
    public static BinaryStarSocketBuilder buildBinaryStarSocket() {
        return ContextFactory.context().buildBinaryStarSocket();
    }

    /**
     * Create a new BeaconReactor, which will send and receive UDP beacons
     * on a broadcast address, with event-driven handling of received beacons.
     * 
     * @return A builder for constructing a BeaconReactor
     */
    public static BeaconReactorBuilder buildBeaconReactor() {
        return ContextFactory.context().buildBeaconReactor();
    }
}
