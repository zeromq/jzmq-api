package org.zeromq.jzmq;

import org.zeromq.api.Pollable;
import org.zeromq.api.Socket;

public class Poller implements Pollable {
    private final Socket socket;

    public Poller(Socket socket) {
        this.socket = socket;
    }
}
