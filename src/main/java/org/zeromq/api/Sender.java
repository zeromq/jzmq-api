package org.zeromq.api;

import java.nio.ByteBuffer;

/**
 * 
 */
public interface Sender {
    boolean send(byte[] buf);

    boolean send(byte[] buf, MessageFlag flag);

    boolean send(byte[] buf, int offset, int length, MessageFlag flag);

    boolean sendZeroCopy(ByteBuffer buf, int length, MessageFlag flag);

    boolean send(Message message);
}
