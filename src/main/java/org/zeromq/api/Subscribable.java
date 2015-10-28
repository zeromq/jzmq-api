package org.zeromq.api;

import org.zeromq.jzmq.sockets.SocketBuilder;

public interface Subscribable {
    SocketBuilder subscribe(byte[] data);
    SocketBuilder subscribeAll();
}
