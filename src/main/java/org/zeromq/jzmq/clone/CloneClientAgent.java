package org.zeromq.jzmq.clone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.Backgroundable;
import org.zeromq.api.Context;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.Message;
import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CloneClientAgent implements Backgroundable {
    private static final Logger log = LoggerFactory.getLogger(CloneClientAgent.class);

    private static final String KTHXBAI = "KTHXBAI";
    private static final String ICANHAZ = "ICANHAZ?";
    private static final String HUGZ = "HUGZ";

    private final ManagedContext context;
    private final Reactor reactor;

    private List<Server> servers = new ArrayList<>(2);
    private Map<String, String> map = new LinkedHashMap<>();

    private Socket pipe;
    private Socket snapshot;
    private Socket subscriber;
    private Socket publisher;
    private String subtree;
    private long sequence;
    private long heartbeatInterval;
    private long serverExpiry;

    public CloneClientAgent(ManagedContext context, long heartbeatInterval) {
        this.context = context;
        this.reactor = context.buildReactor().build();
        this.heartbeatInterval = heartbeatInterval;
    }

    @Override
    public void run(Context context, Socket pipe) {
        updateExpiry();

        this.pipe = pipe;
        reactor.addPollable(context.newPollable(pipe, PollerType.POLL_IN), new PipeHandler());
        reactor.start();
    }

    private void updateExpiry() {
        this.serverExpiry = System.currentTimeMillis() + heartbeatInterval * 2;
    }

    @Override
    public void onClose() {
        reactor.stop();
    }

    private void onConnect(Message message) {
        assert servers.size() < 2;
        assert subtree != null;

        String address = message.popString();
        int port = message.popInt();
        servers.add(new Server(address, port));

        if (servers.size() == 2) {
            Server primary = servers.get(0);
            Server backup = servers.get(1);
            snapshot = context.buildBinaryStarSocket()
                .withHeartbeatInterval(heartbeatInterval)
                .connect(primary.getSnapshot(), backup.getSnapshot());
            subscriber = context.buildSocket(SocketType.SUB)
                .asSubscribable()
                .subscribe(subtree.getBytes(Message.CHARSET))
                .subscribe("HUGZ".getBytes(Message.CHARSET))
                .connect(primary.getSubscriber(), backup.getSubscriber());
            publisher = context.buildSocket(SocketType.PUB)
                .connect(primary.getPublisher(), backup.getPublisher());

            reactor.addTimer(heartbeatInterval, -1, new CheckExpiryHandler());
            reactor.addPollable(context.newPollable(subscriber, PollerType.POLL_IN), new SubscriberHandler());
            reactor.addPollable(context.newPollable(snapshot, PollerType.POLL_IN), new SnapshotHandler());

            log.info("Requesting initial snapshot...");
            requestSnapshot();
        }
    }

    private void onSubtree(Message message) {
        subtree = message.popString();
    }

    private void onGet(Message message) {
        String key = message.popString();
        String value = map.get(key);
        if (value == null) {
            value = "";
        }

        pipe.send(new Message(value));
    }

    private void onGetAll() {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            pipe.send(new Message(entry.getKey()).addString(entry.getValue()));
        }

        pipe.send(new Message(KTHXBAI).addEmptyFrame());
    }

    private void onSet(Message message) {
        String key = message.popString();
        String value = message.popString();
        long ttl = message.popLong();

        CloneMessage m = new CloneMessage();
        m.setKey(key);
        m.setValue(value.getBytes(Message.CHARSET));
        m.setRandomUuid();
        m.ttl(ttl * 1000L);

        m.send(publisher);
        map.put(key, value);
    }

    private void requestSnapshot() {
        log.debug("Requesting snapshot for subtree {}", subtree);
        snapshot.send(new Message(ICANHAZ).addString(subtree));
    }

    private class PipeHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            Message message = pollable.getSocket().receiveMessage();
            String command = message.popString();
            switch (command) {
                case "SUBTREE":
                    onSubtree(message);
                    break;
                case "CONNECT":
                    onConnect(message);
                    break;
                case "GET":
                    onGet(message);
                    break;
                case "GETALL":
                    onGetAll();
                    break;
                case "SET":
                    onSet(message);
                    break;
            }
        }
    }

    private class CheckExpiryHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            if (serverExpiry < System.currentTimeMillis()) {
                log.info("Server expiry reached, requesting new snapshot...");
                serverExpiry = Long.MAX_VALUE;
                map.clear();
                requestSnapshot();
            }
        }
    }

    private class SnapshotHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            CloneMessage message = CloneMessage.receive(snapshot);
            if (message.getKey().equals(KTHXBAI)) {
                log.info("Received snapshot: {}", message.getSequence());
                sequence = message.getSequence();
                updateExpiry();
            } else {
                if (message.getValue() != null) {
                    map.put(message.getKey(), new String(message.getValue(), Message.CHARSET));
                } else {
                    map.remove(message.getKey());
                }
            }
        }
    }

    private class SubscriberHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable) {
            CloneMessage message = CloneMessage.receive(subscriber);
            updateExpiry();

            // Discard out-of-sequence updates, incl. hugz
            if (message.getKey().equals(HUGZ)) {
                return;
            } else if (message.getSequence() <= sequence) {
                log.debug("Discarding out of sequence update: [{}] key={}", message.getSequence(), message.getKey());
                return;
            }

            sequence = message.getSequence();
            if (message.getValue() == null) {
                map.remove(message.getKey());
            } else if (message.getKey().startsWith(subtree)) {
                map.put(message.getKey(), new String(message.getValue(), Message.CHARSET));
            }
        }
    }

    private static class Server {
        public String address;
        public int port;

        public Server(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public String getSnapshot() {
            return String.format("tcp://%s:%d", address, port);
        }

        public String getSubscriber() {
            return String.format("tcp://%s:%d", address, port + 1);
        }

        public String getPublisher() {
            return String.format("tcp://%s:%d", address, port + 2);
        }
    }
}
