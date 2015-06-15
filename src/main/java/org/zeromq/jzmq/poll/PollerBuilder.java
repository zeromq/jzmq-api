package org.zeromq.jzmq.poll;

import org.zeromq.api.PollListener;
import org.zeromq.api.Pollable;
import org.zeromq.api.Poller;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;
import org.zeromq.jzmq.ManagedContext;

import java.util.HashMap;
import java.util.Map;

public class PollerBuilder {

    private final ManagedContext context;
    private Map<Pollable, PollListener> pollablesAndListeners = new HashMap<Pollable, PollListener>();

    public PollerBuilder(ManagedContext context) {
        this.context = context;
    }

    public PollerBuilder withPollable(Pollable pollable, PollListener listener) {
        pollablesAndListeners.put(pollable, listener);
        return this;
    }

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

    public Poller create() {
        return new PollerImpl(context, pollablesAndListeners);
    }
}
