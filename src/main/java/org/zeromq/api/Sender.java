package org.zeromq.api;

import java.nio.ByteBuffer;

/**
 * 
 */
public interface Sender {
    public boolean send(byte[] buf);

    public boolean send(byte[] buf, MessageFlag flag);

    public boolean send(byte[] buf, int offset, int length, MessageFlag flag);

    public boolean sendZeroCopy(ByteBuffer buf, int length, MessageFlag flag);
}
