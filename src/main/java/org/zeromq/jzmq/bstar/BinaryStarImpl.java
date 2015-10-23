package org.zeromq.jzmq.bstar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.api.BinaryStar;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.PollerType;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.api.ZInteger;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.sockets.SocketBuilder;

public class BinaryStarImpl implements BinaryStar {
    private static final Logger log = LoggerFactory.getLogger(BinaryStarImpl.class);

    /**
     * We send state information this often. If peer doesn't respond in two heartbeats, it is 'dead'.
     */
    private static final long BSTAR_HEARTBEAT = 1000;

    private final ManagedContext context;
    private final Reactor reactor;
    private final Socket statePub;
    private final Socket stateSub;
    private final ZInteger stateBuf = new ZInteger();

    private final Mode mode;
    private State state;
    private long peerExpiry;

    private LoopHandler activeHandler;
    private Object[] activeArgs;

    private LoopHandler voterHandler;
    private Object[] voterArgs;

    private LoopHandler passiveHandler;
    private Object[] passiveArgs;

    /**
     * This is the constructor for our {@link BinaryStarImpl} class. We have to tell it
     * whether we're primary or backup server, as well as our local and
     * remote endpoints to bind and connect to.
     * 
     * @param mode The connection mode, either primary or backup
     * @param local The local socket endpoint for publishing events
     * @param remote The remote socket endpoint for subscribing to events
     */
    public BinaryStarImpl(ManagedContext context, Mode mode, String local, String remote) {
        // Initialize the Binary Star
        this.context = context;
        this.mode = mode;
        this.state = (mode == Mode.PRIMARY)
            ? State.PRIMARY_CONNECTING
            : State.BACKUP_CONNECTING;

        // Create publisher for state going to peer
        this.statePub = context.buildSocket(SocketType.PUB)
            .bind(local);

        // Create subscriber for state coming from peer
        this.stateSub = context.buildSocket(SocketType.SUB)
            .asSubscribable().subscribe(ZMQ.SUBSCRIPTION_ALL)
            .connect(remote);

        // Set-up basic reactor events
        this.reactor = context.buildReactor()
            .withTimerRepeating(BSTAR_HEARTBEAT, SEND_STATE, this)
            .withInPollable(stateSub, RECEIVE_STATE, this)
            .build();
    }

    /**
     * Start the configured reactor.
     */
    @Override
    public void start() {
        assert (voterHandler != null);

        updatePeerExpiry();
        reactor.start();
    }

    /**
     * Stop the reactor.
     */
    @Override
    public void stop() {
        reactor.stop();
    }

    /**
     * This method registers a client voter socket. Messages received
     * on this socket provide the CLIENT_REQUEST events for the Binary Star
     * FSM and are passed to the provided application handler. We require
     * exactly one voter per {@link BinaryStarImpl} instance.
     *
     * @param socket The client socket
     */
    @Override
    public void registerVoterSocket(Socket socket) {
        log.debug("Registering voter socket");
        reactor.addPollable(context.newPollable(socket, PollerType.POLL_IN), VOTER_READY, this);
    }

    /**
     * Register handlers to be called each time there's a state change.
     * 
     * @param handler The handler for client events
     * @param args Arguments passed to the handler
     */
    @Override
    public void setVoterHandler(LoopHandler handler, Object... args) {
        this.voterHandler = handler;
        this.voterArgs = args;
    }

    /**
     * Register handlers to be called each time there's a state change.
     *
     * @param handler The handler for state change events
     * @param args Arguments passed to the handler
     */
    @Override
    public void setActiveHandler(LoopHandler handler, Object... args) {
        this.activeHandler = handler;
        this.activeArgs = args;
    }

    /**
     * Register handlers to be called each time there's a state change.
     *
     * @param handler The handler for state change events
     * @param args Arguments passed to the handler
     */
    @Override
    public void setPassiveHandler(LoopHandler handler, Object... args) {
        this.passiveHandler = handler;
        this.passiveArgs = args;
    }

    /**
     * This method returns the underlying Reactor, so we can add
     * additional timers and readers.
     * 
     * @return The underlying Reactor
     */
    @Override
    public Reactor getReactor() {
        return reactor;
    }

    private void updatePeerExpiry() {
        peerExpiry = System.currentTimeMillis() + BSTAR_HEARTBEAT * 2;
    }

    private void fireHandler(LoopHandler handler, Object... args) {
        if (handler != null) {
            handler.execute(reactor, null, args);
        }
    }

    private boolean handleEvent(Event event) {
        /*
         * Binary Star finite state machine (applies event to state).
         * Returns false if there was an exception, true if event was valid.
         */
        boolean result = true;

        if (state == State.PRIMARY_CONNECTING) {
             /*
              * Primary server is waiting for peer to connect.
              * Accepts CLIENT_REQUEST events in this state.
              */
            if (event == Event.PEER_BACKUP) {
                log.info("Connected to backup (passive), ready as active");
                state = State.ACTIVE;

                fireHandler(activeHandler, activeArgs);
            } else if (event == Event.PEER_ACTIVE) {
                log.info("Connected to backup (active), ready as passive");
                state = State.PASSIVE;

                fireHandler(passiveHandler, passiveArgs);
            } else if (event == Event.CLIENT_REQUEST) {
                 /*
                  * Allow client requests to turn us into the active if we've
                  * waited sufficiently long to believe the backup is not
                  * currently acting as active (i.e., after a failover).
                  */
                assert (peerExpiry > 0);
                if (System.currentTimeMillis() >= peerExpiry) {
                    log.info("Request from client, ready as active");
                    state = State.ACTIVE;

                    fireHandler(activeHandler, activeArgs);
                } else {
                     /*
                      * Don't respond to clients yet - it's possible we're
                      * performing a failback and the backup is currently active.
                      */
                    result = false;
                }
            }
        } else if (state == State.BACKUP_CONNECTING) {
             /*
              * Backup server is waiting for peer to connect.
              * Does not accept CLIENT_REQUEST events in this state.
              */
            if (event == Event.PEER_ACTIVE) {
                log.info("Connected to primary (active), ready as passive");
                state = State.PASSIVE;

                fireHandler(passiveHandler, passiveArgs);
            } else if (event == Event.CLIENT_REQUEST) {
                 /*
                  *  Reject client connections when acting as backup.
                  */
                result = false;
            }
        } else if (state == State.ACTIVE) {
             /*
              * Server is active.
              * Accepts CLIENT_REQUEST events in this state.
              */
            if (event == Event.PEER_ACTIVE) {
                 /*
                  * Two actives would mean split-brain.
                  */
                log.error("Fatal error: Dual actives, aborting...");
                result = false;
            }
        } else if (state == State.PASSIVE) {
             /*
              * Server is passive.
              * CLIENT_REQUEST events can trigger failover if peer looks dead.
              */
            if (event == Event.PEER_PRIMARY) {
                 /*
                  * Peer is restarting - become active, peer will go passive.
                  */
                log.info("Primary (passive) is restarting, ready as active");
                state = State.ACTIVE;

                fireHandler(activeHandler, activeArgs);
            } else if (event == Event.PEER_BACKUP) {
                 /*
                  * Peer is restarting - become active, peer will go passive.
                  */
                log.info("Backup (passive) is restarting, ready as active");
                state = State.ACTIVE;

                fireHandler(activeHandler, activeArgs);
            } else if (event == Event.PEER_PASSIVE) {
                 /*
                  * Two passives would mean cluster would be non-responsive.
                  */
                log.error("Fatal error: Dual passives, aborting...");
                result = false;
            } else if (event == Event.CLIENT_REQUEST) {
                 /*
                  * Peer becomes active if timeout has passed.
                  * It's the client request that triggers the failover.
                  */
                assert (peerExpiry > 0);
                if (System.currentTimeMillis () >= peerExpiry) {
                     /*
                      * If peer is dead, switch to the active state.
                      */
                    log.info("Failover successful, ready as active");
                    state = State.ACTIVE;
                } else {
                     /*
                      * If peer is alive, reject connections.
                      */
                    result = false;
                }

                // Call state change handler if necessary
                if (state == State.ACTIVE) {
                    fireHandler(activeHandler, activeArgs);
                }
            }
        }

        return result;
    }

    /**
     * Publish our state to peer.
     */
    private final LoopHandler SEND_STATE = new LoopHandler() {
        @Override
        public void execute(Reactor reactor, Socket socket, Object... args) {
            stateBuf.put(state.ordinal()).send(statePub);
        }
    };

    /**
     * Receive state from peer, execute finite state machine
     */
    private final LoopHandler RECEIVE_STATE = new LoopHandler() {
        @Override
        public void execute(Reactor reactor, Socket socket, Object... args) {
            int ordinal = stateBuf.receive(stateSub);
            assert (ordinal >= 0 && ordinal < Event.values().length);
            updatePeerExpiry();

            Event event = Event.values()[ordinal];
            if (!handleEvent(event)) {
                log.warn("Received fatal error: Restarting...");
                state = (mode == Mode.PRIMARY)
                    ? State.PRIMARY_CONNECTING
                    : State.BACKUP_CONNECTING;
            }
        }
    };

    /**
     * Application wants to speak to us, see if it's possible.
     */
    private final LoopHandler VOTER_READY = new LoopHandler() {
        @Override
        public void execute(Reactor reactor, Socket socket, Object... args) {
            // If server can accept input now, call applicable handler
            if (handleEvent(Event.CLIENT_REQUEST)) {
                voterHandler.execute(reactor, socket, voterArgs);
            } else {
                // Destroy waiting message, no-one to read it
                socket.receiveMessage();
            }
        }
    };
}
