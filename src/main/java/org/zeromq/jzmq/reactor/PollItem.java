package org.zeromq.jzmq.reactor;

import org.zeromq.api.LoopHandler;
import org.zeromq.api.PollListener;
import org.zeromq.api.Pollable;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    public void handleIn(SelectableChannel channel) {
        execute(channel);
    }

    @Override
    public void handleOut(SelectableChannel channel) {
        execute(channel);
    }

    @Override
    public void handleError(SelectableChannel channel) {
        execute(channel);
    }

    private void execute(Socket socket) {
        handler.execute(reactor, socket, args);
    }

    private void execute(SelectableChannel channel) {
        // HACK: Add channel to list of arguments
        // TODO: Add another method to LoopHandler?
        List<Object> newargs = new ArrayList<>(Arrays.asList(args));
        newargs.add(channel);
        handler.execute(reactor, null, newargs.toArray());
    }
}