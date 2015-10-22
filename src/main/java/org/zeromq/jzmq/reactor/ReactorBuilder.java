package org.zeromq.jzmq.reactor;

import org.zeromq.api.LoopHandler;
import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;
import org.zeromq.jzmq.ManagedContext;

public class ReactorBuilder {
    private final ManagedContext context;
    private final ReactorImpl reactor;

    public ReactorBuilder(ManagedContext context) {
        this.context = context;
        this.reactor = newReactor();
    }

    public ReactorBuilder withPollable(Pollable pollable, LoopHandler handler, Object... args) {
        reactor.addPollable(pollable, handler, args);
        return this;
    }

    public ReactorBuilder withInPollable(Socket socket, LoopHandler handler, Object... args) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN), handler, args);
    }

    public ReactorBuilder withOutPollable(Socket socket, LoopHandler handler, Object... args) {
        return withPollable(context.newPollable(socket, PollerType.POLL_OUT), handler, args);
    }

    public ReactorBuilder withErrorPollable(Socket socket, LoopHandler handler, Object... args) {
        return withPollable(context.newPollable(socket, PollerType.POLL_ERROR), handler, args);
    }

    public ReactorBuilder withInOutPollable(Socket socket, LoopHandler handler, Object... args) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN, PollerType.POLL_OUT), handler, args);
    }

    public ReactorBuilder withAllPollable(Socket socket, LoopHandler handler, Object... args) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN, PollerType.POLL_OUT, PollerType.POLL_ERROR), handler, args);
    }

    public ReactorBuilder withTimer(long initialDelay, int numIterations, LoopHandler handler, Object... args) {
        reactor.addTimer(initialDelay, numIterations, handler, args);
        return this;
    }

    public ReactorBuilder withTimerOnce(long initialDelay, LoopHandler handler, Object... args) {
        return withTimer(initialDelay, 1, handler, args);
    }

    public ReactorBuilder withTimerRepeating(long initialDelay, LoopHandler handler, Object... args) {
        return withTimer(initialDelay, -1, handler, args);
    }

    public Reactor build() {
        return reactor;
    }

    public void start() {
        newReactor().start();
    }

    public void run() {
        newReactor().run();
    }

    private ReactorImpl newReactor() {
        return new ReactorImpl(context);
    }
}
