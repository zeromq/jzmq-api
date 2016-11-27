package org.zeromq.jzmq.clone;

import org.zeromq.api.CloneClient;
import org.zeromq.api.Message;
import org.zeromq.api.Socket;
import org.zeromq.jzmq.ManagedContext;

import java.util.LinkedHashMap;
import java.util.Map;

public class CloneClientImpl implements CloneClient {
    private static final Message.Frame SUBTREE = new Message.Frame("SUBTREE");
    private static final Message.Frame CONNECT = new Message.Frame("CONNECT");
    private static final Message.Frame SET = new Message.Frame("SET");
    private static final Message.Frame GET = new Message.Frame("GET");
    private static final Message.Frame GETALL = new Message.Frame("GETALL");

    private final Socket pipe;

    public CloneClientImpl(ManagedContext context, long heartbeatInterval) {
        this.pipe = context.fork(new CloneClientAgent(context, heartbeatInterval));
    }

    @Override
    public void subscribe(String subtree) {
        pipe.send(new Message(SUBTREE).addString(subtree));
    }

    @Override
    public void connect(String address, int port) {
        pipe.send(new Message(CONNECT).addString(address).addInt(port));
    }

    @Override
    public void set(String key, String value) {
        set(key, value, Integer.MAX_VALUE);
    }

    @Override
    public void set(String key, String value, long ttl) {
        pipe.send(new Message(SET).addString(key).addString(value).addLong(ttl));
    }

    @Override
    public String get(String key) {
        pipe.send(new Message(GET).addString(key));

        String value = pipe.receiveMessage().popString();
        if (value.isEmpty()) {
            value = null;
        }

        return value;
    }

    @Override
    public Map<String, String> getAll() {
        pipe.send(new Message(GETALL));

        Map<String, String> map = new LinkedHashMap<>();
        while (true) {
            Message message = pipe.receiveMessage();
            String key = message.popString(), value = message.popString();
            if (key.equals("KTHXBAI")) {
                break;
            }

            map.put(key, value);
        }

        return map;
    }

    @Override
    public void close() {
        pipe.close();
    }
}
