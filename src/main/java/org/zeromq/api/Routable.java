package org.zeromq.api;

import org.zeromq.jzmq.sockets.SocketBuilder;

/**
 * A socket builder for a router socket.
 */
public interface Routable {
    /**
     * Set the 'router mandatory' socket option.
     * 
     * @return This builder object
     */
    SocketBuilder withRouterMandatory();
}
