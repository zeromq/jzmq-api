package org.zeromq.api;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UdpBeacon {
    public static final int BEACON_SIZE = 21; // version(1) + uuid(16) + port(2) + protocol_size(2)

    private final byte[] protocol;
    private final byte version;
    private final UUID uuid;
    private final String identity;
    private final int port;

    private ByteBuffer buffer;

    public UdpBeacon(ByteBuffer buffer) {
        int blockSize = buffer.getShort();
        byte[] protocol = new byte[blockSize];
        buffer.get(protocol);
        byte version = buffer.get();
        long msb = buffer.getLong();
        long lsb = buffer.getLong();
        UUID uuid = new UUID(msb, lsb);
        int port = buffer.getShort();
        if (port < 0)
            port = (0xffff) & port;

        this.protocol = protocol;
        this.version = version;
        this.uuid = uuid;
        this.port = port;
        this.identity = uuid.toString().replace("-", "").toUpperCase();
    }

    public UdpBeacon(byte version, String protocol, int port) {
        this(version, protocol, UUID.randomUUID(), port);
    }

    public UdpBeacon(int version, String protocol, UUID uuid, int port) {
        this.protocol = protocol.getBytes(Message.CHARSET);
        this.version = (byte) version;
        this.uuid = uuid;
        this.port = port;
        this.identity = uuid.toString().replace("-", "").toUpperCase();
    }

    public String getProtocol() {
        return new String(protocol, Message.CHARSET);
    }

    public int getVersion() {
        return version;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getIdentity() {
        return identity;
    }

    public int getPort() {
        return port;
    }

    public ByteBuffer getBuffer() {
        if (buffer == null) {
            buffer = ByteBuffer.allocate(calculateSize());
            buffer.putShort((short) protocol.length);
            buffer.put(protocol);
            buffer.put(version);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            buffer.putShort((short) port);
            buffer.flip();
        }

        buffer.rewind();
        return buffer;
    }

    private int calculateSize() {
        return BEACON_SIZE + protocol.length;
    }
}
