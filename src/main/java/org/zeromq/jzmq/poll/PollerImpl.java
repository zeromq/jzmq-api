package org.zeromq.jzmq.poll;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.api.PollListener;
import org.zeromq.api.Pollable;
import org.zeromq.api.Poller;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;
import org.zeromq.api.exception.ZMQExceptions;
import org.zeromq.jzmq.ManagedContext;

import java.nio.channels.SelectableChannel;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class PollerImpl implements Poller {
    private final Map<Pollable, PollListener> pollables;
    private final ZMQ.Poller poller;

    public PollerImpl(ManagedContext context, Map<Pollable, PollListener> pollables) {
        this.poller = context.newZmqPoller(pollables.size());
        this.pollables = new LinkedHashMap<>(pollables);
        for (Pollable pollable : pollables.keySet()) {
            register(pollable);
        }
    }

    private int sumOptions(Pollable pollable) {
        EnumSet<PollerType> options = pollable.getOptions();
        int sum = 0;
        for (PollerType option : options) {
            sum |= option.getType();
        }
        return sum;
    }

    @Override
    public void poll(long timeoutMillis) {
        int numberOfObjects;
        try {
            numberOfObjects = poller.poll(timeoutMillis);
            if (numberOfObjects == 0) {
                return;
            }

            // simulate ETERM to make JeroMQ act like jzmq, which no longer returns -1
            if (numberOfObjects < 0) {
                throw new ZMQException("Simulated ETERM error", (int) ZMQ.Error.ETERM.getCode());
            }
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }

        int index = 0;
        for (Map.Entry<Pollable, PollListener> entry : pollables.entrySet()) {
            Pollable pollable = entry.getKey();
            PollListener listener = entry.getValue();
            if (poller.pollin(index))
                listener.handleIn(pollable);
            if (poller.pollout(index))
                listener.handleOut(pollable);
            if (poller.pollerr(index))
                listener.handleError(pollable);

            index++;
        }
    }

    @Override
    public void poll() {
        poll(-1);
    }

    @Override
    public int enable(Socket socket) {
        int result = -1;
        Pollable pollable = pollable(socket);
        if (pollable != null) {
            result = register(pollable);
        }

        return result;
    }

    @Override
    public boolean disable(Socket socket) {
        Pollable pollable = pollable(socket);
        if (pollable != null) {
            poller.unregister(socket.getZMQSocket());
        }

        return (pollable != null);
    }

    @Override
    public int enable(SelectableChannel channel) {
        int result = -1;
        Pollable pollable = pollable(channel);
        if (pollable != null) {
            result = register(pollable);
        }

        return result;
    }

    @Override
    public boolean disable(SelectableChannel channel) {
        Pollable pollable = pollable(channel);
        if (pollable != null) {
            poller.unregister(channel);
        }

        return (pollable != null);
    }

    @Override
    public int register(Pollable pollable, PollListener listener) {
        pollables.put(pollable, listener);
        return register(pollable);
    }

    private int register(Pollable pollable) {
        if (pollable.getChannel() != null) {
            return poller.register(pollable.getChannel(), sumOptions(pollable));
        } else {
            return poller.register(pollable.getSocket().getZMQSocket(), sumOptions(pollable));
        }
    }

    private Pollable pollable(Socket socket) {
        Pollable result = null;
        for (Pollable pollable : pollables.keySet()) {
            if (pollable.getSocket() == socket) {
                result = pollable;
                break;
            }
        }
        return result;
    }

    private Pollable pollable(SelectableChannel channel) {
        Pollable result = null;
        for (Pollable pollable : pollables.keySet()) {
            if (pollable.getChannel() == channel) {
                result = pollable;
                break;
            }
        }
        return result;
    }
}
