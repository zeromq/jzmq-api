package org.zeromq.jzmq.reactor;

import org.zeromq.api.LoopHandler;
import org.zeromq.api.PollAdapter;
import org.zeromq.api.Pollable;
import org.zeromq.api.Reactor;

class PollItem extends PollAdapter {
    private Reactor reactor;

    public Pollable pollable;
    public LoopHandler handler;
    public Object[] args;

    public PollItem(Reactor reactor, Pollable pollable, LoopHandler handler) {
        this.reactor = reactor;
        this.pollable = pollable;
        this.handler = handler;
    }

    @Override
    public void handleIn(Pollable pollable) {
        execute(pollable);
    }

    @Override
    public void handleOut(Pollable pollable) {
        execute(pollable);
    }

    @Override
    public void handleError(Pollable pollable) {
        execute(pollable);
    }

    private void execute(Pollable pollable) {
        handler.execute(reactor, pollable);
    }
}