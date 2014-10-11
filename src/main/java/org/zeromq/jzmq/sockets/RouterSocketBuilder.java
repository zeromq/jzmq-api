package org.zeromq.jzmq.sockets;

import org.zeromq.ZMQ;
import org.zeromq.api.Routable;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class RouterSocketBuilder extends SocketBuilder implements Routable {
    private boolean routerMandatory = false;

    public RouterSocketBuilder(ManagedContext managedContext) {
        super(managedContext, SocketType.ROUTER);
    }

    @Override
    public Socket connect(String url, String... additionalUrls) {
        ZMQ.Socket socket = createConnectableSocketWithStandardSettings();
        socket.setRouterMandatory(routerMandatory);
        connect(socket, url, additionalUrls);
        return newManagedSocket(socket);
    }

    @Override
    public Socket bind(String url, String... additionalUrls) {
        ZMQ.Socket socket = createBindableSocketWithStandardSettings();
        socket.setRouterMandatory(routerMandatory);
        bind(socket, url, additionalUrls);
        return newManagedSocket(socket);
    }

    @Override
    public SocketBuilder withRouterMandatory() {
        routerMandatory = true;
        return this;
    }
}
