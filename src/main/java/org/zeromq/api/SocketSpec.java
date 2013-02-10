package org.zeromq.api;


/**
 * ØMQ Socket specification
 * 
 * Socket options that for Socket type
 */
public interface SocketSpec {
    public SocketType getSocketType();

    public TransportType getTransportType();

    public long getLinger();

    public long getReceiveHighWatermark();

    public long getSendHighWatermark();

    public String getIdentity();

    public long getSendBufferSize();

    public long getReceiveBufferSize();

    public long getMaxMessageSize();

    public long getReceiveTimeout();

    public long getSendTimeout();
}
