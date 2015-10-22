package org.zeromq.jzmq.reactor;

import org.zeromq.api.LoopHandler;
import org.zeromq.api.PollListener;
import org.zeromq.api.Pollable;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;

class PollItem implements PollListener {
    private Reactor reactor;

    public Pollable pollable;
    public LoopHandler handler;
    public Object[] args;

    public PollItem(Reactor reactor, Pollable pollable, LoopHandler handler, Object... args) {
        this.reactor = reactor;
        this.pollable = pollable;
        this.handler = handler;
        this.args = args;
    }

    @Override
    public void handleIn(Socket socket) {
        execute(socket);
    }

    @Override
    public void handleOut(Socket socket) {
        execute(socket);
    }

    @Override
    public void handleError(Socket socket) {
        execute(socket);
    }

    private void execute(Socket socket) {
        handler.execute(reactor, socket, args);
    }
}