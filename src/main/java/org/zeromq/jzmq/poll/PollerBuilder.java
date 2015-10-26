package org.zeromq.jzmq.poll;

import org.zeromq.api.PollListener;
import org.zeromq.api.Pollable;
import org.zeromq.api.Poller;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;
import org.zeromq.jzmq.ManagedContext;

import java.nio.channels.SelectableChannel;
import java.util.LinkedHashMap;
import java.util.Map;

public class PollerBuilder {

    private final ManagedContext context;
    private final Map<Pollable, PollListener> pollablesAndListeners = new LinkedHashMap<>();

    public PollerBuilder(ManagedContext context) {
        this.context = context;
    }

    public PollerBuilder withPollable(Pollable pollable, PollListener listener) {
        pollablesAndListeners.put(pollable, listener);
        return this;
    }

    /*
     * Socket Pollables.
     */

    public PollerBuilder withInPollable(Socket socket, PollListener listener) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN), listener);
    }

    public PollerBuilder withOutPollable(Socket socket, PollListener listener) {
        return withPollable(context.newPollable(socket, PollerType.POLL_OUT), listener);
    }

    public PollerBuilder withErrorPollable(Socket socket, PollListener listener) {
        return withPollable(context.newPollable(socket, PollerType.POLL_ERROR), listener);
    }

    public PollerBuilder withInOutPollable(Socket socket, PollListener listener) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN, PollerType.POLL_OUT), listener);
    }

    public PollerBuilder withAllPollable(Socket socket, PollListener listener) {
        return withPollable(context.newPollable(socket, PollerType.POLL_IN, PollerType.POLL_OUT, PollerType.POLL_ERROR), listener);
    }

    /*
     * SelectableChannel Pollables.
     */

    public PollerBuilder withInPollable(SelectableChannel channel, PollListener listener) {
        return withPollable(context.newPollable(channel, PollerType.POLL_IN), listener);
    }

    public PollerBuilder withOutPollable(SelectableChannel channel, PollListener listener) {
        return withPollable(context.newPollable(channel, PollerType.POLL_OUT), listener);
    }

    public PollerBuilder withErrorPollable(SelectableChannel channel, PollListener listener) {
        return withPollable(context.newPollable(channel, PollerType.POLL_ERROR), listener);
    }

    public PollerBuilder withInOutPollable(SelectableChannel channel, PollListener listener) {
        return withPollable(context.newPollable(channel, PollerType.POLL_IN, PollerType.POLL_OUT), listener);
    }

    public PollerBuilder withAllPollable(SelectableChannel channel, PollListener listener) {
        return withPollable(context.newPollable(channel, PollerType.POLL_IN, PollerType.POLL_OUT, PollerType.POLL_ERROR), listener);
    }

    @Deprecated
    public Poller create() {
        return build();
    }

    public Poller build() {
        return new PollerImpl(context, pollablesAndListeners);
    }
}
