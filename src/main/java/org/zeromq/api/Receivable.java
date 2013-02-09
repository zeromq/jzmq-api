package org.zeromq.api;


/**
 * 
 * @param <T> type t
 */
public interface Receivable<T> {
    public T receive() throws Exception;

    public T receive(MessageFlag flag) throws Exception;
}
