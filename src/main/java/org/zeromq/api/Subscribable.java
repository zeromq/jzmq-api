package org.zeromq.api;

import org.zeromq.jzmq.sockets.SocketBuilder;

/**
 * A socket builder for a subscriber socket.
 */
public interface Subscribable {
    /**
     * Subscribe to the given channel.
     * 
     * @param data The channel prefix, as bytes
     * @return This builder object
     */
    SocketBuilder subscribe(byte[] data);

    /**
     * Subscribe to all messages.
     * 
     * @return This builder object
     */
    SocketBuilder subscribeAll();
}
