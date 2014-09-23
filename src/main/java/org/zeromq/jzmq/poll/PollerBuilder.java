package org.zeromq.jzmq.poll;

import org.zeromq.api.PollListener;
import org.zeromq.api.Pollable;
import org.zeromq.api.Poller;
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

    public Poller create() {
        return new PollerImpl(context, pollablesAndListeners);
    }
}
