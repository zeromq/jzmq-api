package org.zeromq.jzmq.clone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.BinaryStarReactor;
import org.zeromq.api.CloneServer;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.Message;
import org.zeromq.api.MessageFlag;
import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

public class CloneServerImpl implements CloneServer {
    private static final Logger log = LoggerFactory.getLogger(CloneServer.class);

    /**
     * States we can be in.
     */
    private enum State {
        ACTIVE, PASSIVE
    }

    private final String peerAddress;
    private final int peerPort;

    private final ManagedContext context;
    private final Socket publisher;
    private final Socket collector;
    private final Socket channel;
    private final Socket subscriber;
    private final BinaryStarReactor binaryStarReactor;

    private final LoopHandler subscriberHandler = new SubscriberHandler();
    private final CloneMessage kthxbai = new CloneMessage("KTHXBAI");
    private final CloneMessage hugz = new CloneMessage("HUGZ");
    private final Message icanhaz = new Message("ICANHAZ?").addEmptyFrame();

    private long heartbeatInterval;

    private State state;
    private long sequence;
    private Map<String, CloneMessage> snapshot;
    private final Map<UUID, CloneMessage> pending = new HashMap<>();
    private final Queue<CloneMessage> ttlQueue = new PriorityQueue<>(11, CloneMessage.SORT_BY_TTL);

    public CloneServerImpl(ManagedContext context, BinaryStarReactor.Mode mode, String peerAddress, int localPort, int peerPort, int localBstarPort, int peerBstarPort) {
        this.peerAddress = peerAddress;
        this.peerPort = peerPort;

        this.context = context;
        this.channel = context.buildSocket(SocketType.ROUTER)
            .bind(String.format("tcp://*:%d", localPort));
        this.publisher = context.buildSocket(SocketType.PUB)
            .bind(String.format("tcp://*:%d", localPort + 1));
        this.collector = context.buildSocket(SocketType.SUB)
            .asSubscribable().subscribeAll()
            .bind(String.format("tcp://*:%d", localPort + 2));
        this.subscriber = context.buildSocket(SocketType.SUB)
            .asSubscribable().subscribeAll()
            .connect(String.format("tcp://%s:%d", peerAddress, peerPort + 1));

        this.binaryStarReactor = context.buildBinaryStarReactor()
            .withMode(mode)
            .withLocalUrl(String.format("tcp://*:%d", localBstarPort))
            .withRemoteUrl(String.format("tcp://%s:%d", peerAddress, peerBstarPort))
            .withVoterSocket(channel)
            .withVoterHandler(new SnapshotHandler())
            .withActiveHandler(new ActiveHandler())
            .withPassiveHandler(new PassiveHandler())
            .build();

        // Initialize as primary, to forego requesting initial snapshot
        // Passive will request initial snapshot
        if (mode == BinaryStarReactor.Mode.PRIMARY) {
            snapshot = new LinkedHashMap<>();
        }
    }

    public void setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    @Override
    public void start() {
        binaryStarReactor.getReactor().addPollable(context.newPollable(collector, PollerType.POLL_IN), new CollectorHandler());
        binaryStarReactor.getReactor().addTimer(heartbeatInterval, -1, new FlushTtlHandler());
        binaryStarReactor.getReactor().addTimer(heartbeatInterval, -1, new SendHugzHandler());
        binaryStarReactor.start();
    }

    @Override
    public void stop() {
        binaryStarReactor.stop();
    }

    private class SnapshotHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            Message message = channel.receiveMessage();
            if (message != null) {
                // Request is in second frame of message
                byte[] identity = message.popFrame().getData();
                assert message.popFrame().equals(icanhaz.getFirstFrame());

                String prefix = "";
                if (!message.isEmpty()) {
                    prefix = message.popFrame().getString();
                }

                // Send state snapshot to client
                for (Map.Entry<String, CloneMessage> entry : snapshot.entrySet()) {
                    if (entry.getKey().startsWith(prefix)) {
                        channel.send(identity, MessageFlag.SEND_MORE);
                        entry.getValue().send(channel);
                    }
                }

                // Now send END message with sequence number
                log.debug("Sending snapshot: [{}]", sequence);
                channel.send(identity, MessageFlag.SEND_MORE);
                kthxbai.setSequence(sequence);
                kthxbai.setValue(prefix.getBytes(Message.CHARSET));
                kthxbai.send(channel);
            }
        }
    }

    private class CollectorHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            CloneMessage message = CloneMessage.receive(collector);
            if (state == State.ACTIVE) {
                CloneMessage prev = snapshot.put(message.getKey(), message);
                if (prev != null) {
                    ttlQueue.remove(prev);
                }
                ttlQueue.offer(message);

                message.setSequence(++sequence);
                message.send(publisher);
                log.debug("Published update: [{}] {}", sequence, message.getKey());
            } else if (state == State.PASSIVE) {
                // If we already got message from active, drop it, else
                // hold on pending list
                if (pending.containsKey(message.getUuid())) {
                    pending.remove(message.getUuid());
                    pending.put(message.getUuid(), message);
                }
            }
        }
    }

    private class SendHugzHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            // We send a HUGZ message once a second to all subscribers so that they
            // can detect if our server dies. They'll then switch over to the backup
            // server, which will become active:
            hugz.setSequence(sequence);
            hugz.send(publisher);
        }
    }

    private class FlushTtlHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            if (state == State.PASSIVE) {
                return;
            }

            long now = System.currentTimeMillis();
            while (!ttlQueue.isEmpty()
                    && ttlQueue.peek().expiresOn() <= now) {
                // If key-value pair has expired, delete it and publish the
                // fact to listening clients.
                CloneMessage message = ttlQueue.poll();
                message.setValue(null);
                message.setSequence(++sequence);
                snapshot.remove(message.getKey());

                message.send(publisher);
                log.debug("Published expired: [{}] {}", sequence, message.getKey());
            }
        }
    }

    private class ActiveHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            state = State.ACTIVE;
            reactor.cancel(subscriberHandler);

            // Publish pending entries
            for (CloneMessage message : pending.values()) {
                message.setSequence(++sequence);
                snapshot.put(message.getKey(), message);
                ttlQueue.offer(message);

                message.send(publisher);
                log.debug("Published pending: [{}] {}", sequence, message.getKey());
            }

            pending.clear();
        }
    }

    private class PassiveHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            state = State.PASSIVE;
            reactor.addPollable(context.newPollable(subscriber, PollerType.POLL_IN), subscriberHandler);
            if (snapshot != null) {
                snapshot.clear();
            }
            snapshot = null;
        }
    }

    private class SubscriberHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            // Get state snapshot if necessary
            if (snapshot == null) {
                log.info("Requesting snapshot from: tcp://{}:{}", peerAddress, peerPort);
                snapshot = new LinkedHashMap<>();
                Socket client = context.buildSocket(SocketType.DEALER)
                    .connect(String.format("tcp://%s:%d", peerAddress, peerPort));
                client.send(icanhaz);

                CloneMessage message;
                while (true) {
                    message = CloneMessage.receive(client);
                    sequence = message.getSequence();
                    if (message.getKey().equals(kthxbai.getKey())) {
                        break;
                    }

                    snapshot.put(message.getKey(), message);
                    ttlQueue.offer(message);
                }

                client.close();
                log.info("Received snapshot: {}", sequence);
            }

            // Find and remove update off pending list
            CloneMessage message = CloneMessage.receive(subscriber);
            if (message != null && !message.getKey().equals(hugz.getKey())) {
                // If active update came before client update, flip it
                // around, store active update (with sequence) on pending
                // list and use to clear client update when it comes later
                if (!pending.containsKey(message.getUuid())) {
                    pending.put(message.getUuid(), message.clone());
                } else {
                    pending.remove(message.getUuid());
                }

                // If update is more recent than our kvmap, apply it
                if (message.getSequence() > sequence) {
                    sequence = message.getSequence();
                    snapshot.put(message.getKey(), message);
                    ttlQueue.offer(message);
                    log.debug("Received update: [{}] {}", sequence, message.getKey());
                }
            }
        }
    }
}
