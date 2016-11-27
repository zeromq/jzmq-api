package org.zeromq.api;

import java.nio.channels.SelectableChannel;

/**
 * An abstract class for implementing a {@link LoopHandler}.
 */
public class LoopAdapter implements LoopHandler {
    @Override
    public void execute(Reactor reactor, Pollable pollable) {
        execute(reactor, pollable.getSocket());
        execute(reactor, pollable.getChannel());
    }

    protected void execute(Reactor reactor, Socket socket) {
    }

    protected void execute(Reactor reactor, SelectableChannel channel) {
    }
}
