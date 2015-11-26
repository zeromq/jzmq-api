package org.zeromq.api;

import java.nio.channels.SelectableChannel;

/**
 * An abstract class for implementing a {@link LoopHandler}.
 */
public class LoopAdapter implements LoopHandler {
    @Override
    public void execute(Reactor reactor, Pollable pollable, Object... args) {
        execute(reactor, pollable.getSocket(), args);
        execute(reactor, pollable.getChannel(), args);
    }

    protected void execute(Reactor reactor, Socket socket, Object... args) {
    }

    protected void execute(Reactor reactor, SelectableChannel channel, Object... args) {
    }
}
