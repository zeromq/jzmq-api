package org.zeromq.api;

public interface TypedSendable<T> {
    public void send(T t, int flags) throws Exception;
}
