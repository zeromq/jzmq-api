package org.zeromq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.BinaryStarReactor;
import org.zeromq.api.CloneClient;
import org.zeromq.api.CloneServer;
import org.zeromq.api.Socket;
import org.zeromq.jzmq.beacon.BeaconReactorBuilder;
import org.zeromq.jzmq.bstar.BinaryStarReactorBuilder;
import org.zeromq.jzmq.bstar.BinaryStarSocketBuilder;
import org.zeromq.jzmq.clone.CloneClientBuilder;
import org.zeromq.jzmq.clone.CloneServerBuilder;

/**
 * Class containing utility methods for creating objects for different patterns
 * without using a ØMQ Context directly.
 */
public class Patterns {
    private static final Logger log = LoggerFactory.getLogger(Patterns.class);

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
     * Create a ØMQ DEALER Socket, backed by a background agent that is connected
     * to a BinaryStarReactor HA-pair.
     * 
     * @param primaryUrl The primary server's url to connect to
     * @param backupUrl The backup server's url to connect to
     * @return A new Socket connected to an HA-pair
     */
    public static Socket newBinaryStarSocket(String primaryUrl, String backupUrl) {
        return buildBinaryStarSocket().connect(primaryUrl, backupUrl);
    }

    /**
     * Create a new CloneServer, which will create one half of an HA-pair
     * for distributing key/value data to clients.
     * 
     * @return A builder for constructing a CloneServer
     */
    public static CloneServerBuilder buildCloneServer() {
        return ContextFactory.context().buildCloneServer();
    }

    /**
     * Create a new CloneServer, which will create one half of an HA-pair
     * for distributing key/value data to clients.
     * 
     * @param mode The server mode (e.g. PRIMARY, BACKUP)
     * @param primaryAddress The primary server's url to connect to (if mode is BACKUP)
     * @param backupAddress The backup server's url to connect to (if mode is PRIMARY)
     * @return A new CloneServer
     */
    public static CloneServer newCloneServer(BinaryStarReactor.Mode mode, String primaryAddress, String backupAddress) {
        CloneServer cloneServer = buildCloneServer()
            .withMode(mode)
            .withPrimaryAddress(primaryAddress)
            .withBackupAddress(backupAddress)
            .withPrimaryPort(5556)
            .withBackupPort(5566)
            .withPrimaryBinaryStarPort(5003)
            .withBackupBinaryStarPort(5004)
            .build();
        cloneServer.start();
        Runtime.getRuntime().addShutdownHook(new CloneServerShutdownHook(cloneServer));

        return cloneServer;
    }

    /**
     * Create a new CloneClient, connected to an HA-pair, for publishing key/value data
     * to anonymous peers.
     * 
     * @return A builder for constructing a CloneClient
     */
    public static CloneClientBuilder buildCloneClient() {
        return ContextFactory.context().buildCloneClient();
    }

    /**
     * Create a new CloneClient, connected to an HA-pair, for publishing key/value data
     * to anonymous peers.
     * 
     * @param primaryAddress The primary server's url to connect to
     * @param backupAddress The backup server's url to connect to
     * @param subtree The prefix (subtree) to subscribe to
     * @return A new CloneClient
     */
    public static CloneClient newCloneClient(String primaryAddress, String backupAddress, String subtree) {
        return buildCloneClient()
            .withPrimaryAddress(primaryAddress)
            .withBackupAddress(backupAddress)
            .withPrimaryPort(5556)
            .withBackupPort(5566)
            .withSubtree(subtree)
            .build();
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

    private static class CloneServerShutdownHook extends Thread {
        private CloneServer cloneServer;

        public CloneServerShutdownHook(CloneServer cloneServer) {
            this.cloneServer = cloneServer;
        }

        @Override
        public void run() {
            log.info("Stopping CloneServer...");
            cloneServer.stop();
            log.info("CloneServer stopped");
        }
    }
}
