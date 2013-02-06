package org.zeromq.api;


public interface Sendable {
    public void send(byte[] buf) throws Exception;

    public void send(byte[] buf, int offset, SendFlag flag) throws Exception;
}
