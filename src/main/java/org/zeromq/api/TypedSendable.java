package org.zeromq.api;

public interface TypedSendable<T> {
    public byte[] serialize(T t);

    public void send(T t, int flags);
}
