package org.zeromq.jzmq.sockets;

import org.zeromq.ZMQ;
import org.zeromq.api.Routable;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.ManagedSocket;

public class RouterSocketBuilder extends SocketBuilder implements Routable {
    private boolean routerMandatory = false;

    public RouterSocketBuilder(ManagedContext managedContext) {
        super(managedContext, SocketType.ROUTER);
    }

    @Override
    public Socket connect(String url) {
        ZMQ.Socket socket = createConnectableSocketWithStandardSettings();
        socket.setRouterMandatory(routerMandatory);
        socket.connect(url);
        return new ManagedSocket(context, socket);
    }

    @Override
    public Socket bind(String url, String... additionalUrls) {
        ZMQ.Socket socket = createBindableSocketWithStandardSettings();
        socket.setRouterMandatory(routerMandatory);
        bind(socket, url, additionalUrls);
        return new ManagedSocket(context, socket);
    }

    @Override
    public SocketBuilder withRouterMandatory() {
        routerMandatory = true;
        return this;
    }
}
