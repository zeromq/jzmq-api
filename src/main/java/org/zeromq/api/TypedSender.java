package org.zeromq.api;

public interface TypedSender<T> {
    public byte[] serialize(T t);

    public void send(T t, int flags);
}
