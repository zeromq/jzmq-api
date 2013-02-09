package org.zeromq.api;

/**
 * 
 *
 * @param <T> type
 */
public interface TypedReceivable<T> {
    public T deserialize(byte[] buf);

    public T receive() throws Exception;

    public T receive(MessageFlag flag);
}
