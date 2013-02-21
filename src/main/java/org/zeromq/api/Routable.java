package org.zeromq.api;

import org.zeromq.jzmq.sockets.SocketBuilder;

public interface Routable {
    SocketBuilder withRouterMandatory();
}
