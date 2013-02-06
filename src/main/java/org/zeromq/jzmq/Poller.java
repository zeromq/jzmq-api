package org.zeromq.jzmq;

import org.zeromq.api.Socket;


public class Poller {
    private final Socket socket;

    public Poller(Socket socket) {
        this.socket = socket;
    }
}
