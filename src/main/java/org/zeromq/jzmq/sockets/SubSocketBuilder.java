package org.zeromq.jzmq.sockets;

import org.zeromq.ZMQ;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.api.Subscribable;
import org.zeromq.jzmq.ManagedContext;

import java.util.ArrayList;
import java.util.List;

public class SubSocketBuilder extends SocketBuilder implements Subscribable {
    private List<byte[]> subscriptions = new ArrayList<>();

    public SubSocketBuilder(ManagedContext context) {
        super(context, SocketType.SUB);
    }

    @Override
    public SubSocketBuilder subscribe(byte[] data) {
        byte[] subscription = new byte[data.length];
        System.arraycopy(data, 0, subscription, 0, data.length);
        subscriptions.add(subscription);
        return this;
    }

    @Override
    public SubSocketBuilder subscribeAll() {
        return subscribe(new byte[0]);
    }

    @Override
    public Socket connect(String url, String... additionalUrls) {
        if (subscriptions.isEmpty()) {
            throw new IllegalStateException("You must have a SUB socket subscribe to something before you can connect it.");
        }
        ZMQ.Socket socket = createConnectableSocketWithStandardSettings();
        for (byte[] subscription : subscriptions) {
            socket.subscribe(subscription);
        }
        connect(socket, url, additionalUrls);
        return newManagedSocket(socket);
    }
    
    @Override
    public Socket bind(String url, String... additionalUrls) {
        if (subscriptions.isEmpty()) {
            throw new IllegalStateException("You must have a SUB socket subscribe to something before you can bind it.");
        }
        ZMQ.Socket socket = createConnectableSocketWithStandardSettings();
        for (byte[] subscription : subscriptions) {
            socket.subscribe(subscription);
        }
        bind(socket, url, additionalUrls);
        return newManagedSocket(socket);
    }

}
