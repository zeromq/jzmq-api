package org.zeromq.api;


/**
 * 
 * @param <T> type t
 */
public interface Receiver<T> {
    public T receive();

    public T receive(MessageFlag flag);
}
