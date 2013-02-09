package org.zeromq.api;


public interface Sendable {
    public void send(byte[] buf);

    public void send(byte[] buf, int offset, MessageFlag flag);
}
