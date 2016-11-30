package org.zeromq.jzmq.clone;

import org.zeromq.api.Message;
import org.zeromq.api.Socket;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

class CloneMessage implements Cloneable {
    private long sequence;
    private UUID uuid;
    private String key;
    private byte[] value;
    private Map<String, String> properties = new LinkedHashMap<>();

    private long expiresOn;

    public CloneMessage() {
    }

    public CloneMessage(String key) {
        this.key = key;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setRandomUuid() {
        setUuid(UUID.randomUUID());
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void addProperty(String propertyKey, String propertyValue) {
        properties.put(propertyKey, propertyValue);
    }

    public long ttl() {
        String value = properties.get("ttl");
        return (value == null) ? -1 : Long.parseLong(value);
    }

    public void ttl(long ttl) {
        properties.put("ttl", String.valueOf(ttl));
    }

    public long expiresOn() {
        if (expiresOn == 0) {
            long ttl = ttl();
            if (ttl < 0) {
                expiresOn = Long.MAX_VALUE;
            } else {
                expiresOn = System.currentTimeMillis() + ttl;
            }
        }

        return expiresOn;
    }

    @Override
    public CloneMessage clone() {
        CloneMessage clone = null;
        try {
            clone = (CloneMessage) super.clone();
            clone.properties.putAll(properties);
        } catch (CloneNotSupportedException ignored) {
        }

        return clone;
    }

    /**
     * Send a {@link CloneMessage} over the given ØMQ Socket.
     * 
     * @param socket The ØMQ socket
     */
    public void send(Socket socket) {
        Message message = new Message();
        message.addString(key);

        // Add optional sequence
        message.addLong(sequence);

        // Add optional UUID
        if (uuid != null) {
            message.addString(uuid.toString());
        } else {
            message.addEmptyFrame();
        }

        // Add optional properties
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('\n');
        }
        message.addString(sb.toString());

        // Add optional value
        if (value != null) {
            message.addBytes(value);
        } else {
            message.addEmptyFrame();
        }

        socket.send(message);
    }

    /**
     * Receive a {@link CloneMessage} over the given ØMQ Socket.
     * 
     * @param socket The ØMQ socket
     * @return The received message
     */
    public static CloneMessage receive(Socket socket) {
        Message message = socket.receiveMessage();
        assert message.size() == 5;

        String key = message.popString();
        long sequence = message.popLong();
        String uuid = message.popString();
        String[] props = message.popString().trim().split("\n");
        byte[] value = message.popBytes();

        CloneMessage m = new CloneMessage();
        m.setKey(key);
        if (value.length > 0) {
            m.setValue(value);
        }

        // Set sequence
        if (sequence > 0) {
            m.setSequence(sequence);
        }

        // Set optional UUID
        if (uuid.length() > 0) {
            m.setUuid(UUID.fromString(uuid));
        }

        // Decode properties
        if (props.length > 0) {
            for (String prop : props) {
                if (prop.isEmpty()) {
                    continue;
                }

                assert prop.contains("=");
                String[] parts = prop.split("=");
                m.addProperty(parts[0], parts[1]);
            }
        }

        return m;
    }

    /**
     * Comparator used to sort {@link CloneMessage}s by time-to-live.
     */
    public static final Comparator<CloneMessage> SORT_BY_TTL = new Comparator<CloneMessage>() {
        @Override
        public int compare(CloneMessage a, CloneMessage b) {
            return Long.compare(a.expiresOn(), b.expiresOn());
        }
    };
}
