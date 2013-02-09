package org.zeromq.api;

/**
 * ØMQ Socket specification
 * 
 * Socket options that all Socket types support
 */
public interface BaseSocketSpec {
    public SocketType getSocketType();

    public TransportType getTransportType();

    public long getLinger();

    public long getReceiveHighWaterMark();

    public long getSendHighWaterMark();

    public String getIdentity();

    public long getSendBufferSize();

    public long getReceiveBufferSize();

    public long getMaxMessageSize();

    public long getReceiveTimeout();

    public long getSendTimeout();
}
