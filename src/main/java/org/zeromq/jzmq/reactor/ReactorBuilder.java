package org.zeromq.jzmq.reactor;

import org.zeromq.api.LoopHandler;
import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;
import org.zeromq.jzmq.ManagedContext;

import java.nio.channels.SelectableChannel;
import java.util.concurrent.TimeUnit;

public class ReactorBuilder {
    private final ManagedContext context;
    private final ReactorImpl reactor;

    public ReactorBuilder(ManagedContext context) {
        this.context = context;
        this.reactor = new ReactorImpl(context);
    }

    /*
     * Socket Pollables.
     */

    public ReactorBuilder withPollable(Pollable pollable, LoopHandler handler) {
        reactor.addPollable(pollable, handler);
        return this;
    }

    public ReactorBuilder withInPollable(Socket socket, LoopHandler handler) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN), handler);
    }

    public ReactorBuilder withOutPollable(Socket socket, LoopHandler handler) {
        return withPollable(context.newPollable(socket, PollerType.POLL_OUT), handler);
    }

    public ReactorBuilder withErrorPollable(Socket socket, LoopHandler handler) {
        return withPollable(context.newPollable(socket, PollerType.POLL_ERROR), handler);
    }

    public ReactorBuilder withInOutPollable(Socket socket, LoopHandler handler) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN, PollerType.POLL_OUT), handler);
    }

    public ReactorBuilder withAllPollable(Socket socket, LoopHandler handler) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN, PollerType.POLL_OUT, PollerType.POLL_ERROR), handler);
    }

    /*
     * SelectableChannel Pollables.
     */

    public ReactorBuilder withInPollable(SelectableChannel channel, LoopHandler handler) {
        return withPollable(context.newPollable(channel, PollerType.POLL_IN), handler);
    }

    public ReactorBuilder withOutPollable(SelectableChannel channel, LoopHandler handler) {
        return withPollable(context.newPollable(channel, PollerType.POLL_OUT), handler);
    }

    public ReactorBuilder withErrorPollable(SelectableChannel channel, LoopHandler handler) {
        return withPollable(context.newPollable(channel, PollerType.POLL_ERROR), handler);
    }

    public ReactorBuilder withInOutPollable(SelectableChannel channel, LoopHandler handler) {
        return withPollable(context.newPollable(channel, PollerType.POLL_IN, PollerType.POLL_OUT), handler);
    }

    public ReactorBuilder withAllPollable(SelectableChannel channel, LoopHandler handler) {
        return withPollable(context.newPollable(channel, PollerType.POLL_IN, PollerType.POLL_OUT, PollerType.POLL_ERROR), handler);
    }

    /*
     * Timer Pollables.
     */

    public ReactorBuilder withTimer(long initialDelay, int numIterations, LoopHandler handler) {
        reactor.addTimer(initialDelay, numIterations, handler);
        return this;
    }

    public ReactorBuilder withTimerOnce(long initialDelay, LoopHandler handler) {
        return withTimer(initialDelay, 1, handler);
    }

    public ReactorBuilder withTimerOnce(long initialDelay, TimeUnit unit, LoopHandler handler) {
        return withTimer(unit.toMillis(initialDelay), 1, handler);
    }

    public ReactorBuilder withTimerRepeating(long initialDelay, LoopHandler handler) {
        return withTimer(initialDelay, -1, handler);
    }

    public ReactorBuilder withTimerRepeating(long initialDelay, TimeUnit unit, LoopHandler handler) {
        return withTimer(unit.toMillis(initialDelay), -1, handler);
    }

    public Reactor build() {
        return reactor;
    }

    public void start() {
        reactor.start();
    }

    public void run() {
        reactor.run();
    }
}
