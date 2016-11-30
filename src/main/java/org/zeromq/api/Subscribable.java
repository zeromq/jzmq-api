package org.zeromq.api;

import org.zeromq.jzmq.sockets.SubSocketBuilder;

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
    SubSocketBuilder subscribe(byte[] data);

    /**
     * Subscribe to all messages.
     * 
     * @return This builder object
     */
    SubSocketBuilder subscribeAll();
}
