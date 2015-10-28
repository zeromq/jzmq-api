package org.zeromq.api;

/**
 * Reactor implementing the Binary Star pattern, used to construct HA-pairs
 * that can forward messages to an application in an event-driven manner.
 */
public interface BinaryStarReactor {
    /**
     * We send state information this often. If peer doesn't respond in two heartbeats, it is 'dead'.
     */
    long BSTAR_HEARTBEAT = 1000;

    /**
     * Startup modes.
     */
    enum Mode {
        /**
         * Primary node.
         */
        PRIMARY,
        /**
         * Backup node.
         */
        BACKUP
    }

    /**
     * States we can be in at any point in time.
     */
    enum State {
        /**
         * Primary, waiting for peer to connect.
         */
        PRIMARY_CONNECTING,
        /**
         * Backup, waiting for peer to connect.
         */
        BACKUP_CONNECTING,
        /**
         * Active - accepting connections.
         */
        ACTIVE,
        /**
         * Passive - Not accepting connections.
         */
        PASSIVE
    }

    /**
     * Events, which start with the states our peer can be in.
     */
    enum Event {
        /**
         * HA peer is pending primary.
         */
        PEER_PRIMARY,
        /**
         * HA peer is pending backup.
         */
        PEER_BACKUP,
        /**
         * HA peer is active.
         */
        PEER_ACTIVE,
        /**
         * HA peer is passive.
         */
        PEER_PASSIVE,
        /**
         * Client makes request.
         */
        CLIENT_REQUEST
    }

    /**
     * Start the underlying Reactor.
     */
    void start();

    /**
     * Stop the underlying Reactor.
     */
    void stop();

    /**
     * Register a client voter socket. Only one socket can be registered.
     *
     * @param socket The client socket
     */
    void registerVoterSocket(Socket socket);

    /**
     * Register a voter handler to be called each time the application receives a message.
     *
     * @param handler The handler for client events
     * @param args Arguments passed to the handler
     */
    void setVoterHandler(LoopHandler handler, Object... args);

    /**
     * Register a handler to be called each time there's a state change.
     *
     * @param handler The handler for state change events
     * @param args Arguments passed to the handler
     */
    void setActiveHandler(LoopHandler handler, Object... args);

    /**
     * Register a handler to be called each time there's a state change.
     *
     * @param handler The handler for state change events
     * @param args Arguments passed to the handler
     */
    void setPassiveHandler(LoopHandler handler, Object... args);

    /**
     * Get the underlying {@link Reactor}.
     * 
     * @return The underlying Reactor
     */
    Reactor getReactor();

    /**
     * Set the heartbeat interval used to detect peer outage.
     * 
     * @param heartbeatInterval The heartbeat interval, in milliseconds
     */
    void setHeartbeatInterval(long heartbeatInterval);
}
