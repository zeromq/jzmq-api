package org.zeromq.api;

/**
 * 
 *
 * @param <T> type
 */
public interface TypedReceiver<T> {
    public T deserialize(byte[] buf);

    public T receive();

    public T receive(MessageFlag flag);
}
