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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class PollerImpl implements Poller {
    private final ZMQ.Poller poller;
    private final List<Pollable> pollables = new ArrayList<>();
    private final List<PollListener> listeners = new ArrayList<>();

    public PollerImpl(ManagedContext context, Map<Pollable, PollListener> pollableMap) {
        this.poller = context.newZmqPoller(pollables.size());
        for (Map.Entry<Pollable, PollListener> entry : pollableMap.entrySet()) {
            pollables.add(entry.getKey());
            listeners.add(entry.getValue());
            register(entry.getKey());
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
        try {
            int numberOfObjects = poller.poll(timeoutMillis);
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

        for (int index = 0; index < pollables.size(); index++) {
            Pollable pollable = pollables.get(index);
            PollListener listener = listeners.get(index);
            if (pollable != null) {
                if (poller.pollin(index))
                    listener.handleIn(pollable);
                if (poller.pollout(index))
                    listener.handleOut(pollable);
                if (poller.pollerr(index))
                    listener.handleError(pollable);
            }
        }
    }

    @Override
    public void poll() {
        poll(-1);
    }

    @Override
    public int enable(Socket socket) {
        return register(pollable(socket));
    }

    @Override
    public boolean disable(Socket socket) {
        return unregister(pollable(socket));
    }

    @Override
    public int enable(SelectableChannel channel) {
        return register(pollable(channel));
    }

    @Override
    public boolean disable(SelectableChannel channel) {
        return unregister(pollable(channel));
    }

    @Override
    public int register(Pollable pollable, PollListener listener) {
        // find a free slot, or add a new one
        int index = pollables.indexOf(null);
        if (index < 0) {
            pollables.add(pollable);
            listeners.add(listener);
        } else {
            pollables.set(index, pollable);
            listeners.set(index, listener);
        }

        return register(pollable);
    }

    @Override
    public boolean unregister(Socket socket) {
        Pollable pollable = pollable(socket);
        return unregister(pollable)
            && removePollable(pollable);
    }

    @Override
    public boolean unregister(SelectableChannel channel) {
        Pollable pollable = pollable(channel);
        return unregister(pollable)
            && removePollable(pollable);
    }
    private int register(Pollable pollable) {
        int result = -1;
        if (pollable != null) {
            if (pollable.getChannel() != null) {
                result = poller.register(pollable.getChannel(), sumOptions(pollable));
            } else {
                result = poller.register(pollable.getSocket().getZMQSocket(), sumOptions(pollable));
            }
        }

        return result;
    }

    private boolean unregister(Pollable pollable) {
        if (pollable != null) {
            if (pollable.getChannel() != null) {
                poller.unregister(pollable.getChannel());
            } else {
                poller.unregister(pollable.getSocket().getZMQSocket());
            }
        }

        return pollable != null;
    }

    private Pollable pollable(Socket socket) {
        Pollable result = null;
        for (Pollable pollable : pollables) {
            if (pollable.getSocket() == socket) {
                result = pollable;
                break;
            }
        }
        return result;
    }

    private Pollable pollable(SelectableChannel channel) {
        Pollable result = null;
        for (Pollable pollable : pollables) {
            if (pollable.getChannel() == channel) {
                result = pollable;
                break;
            }
        }
        return result;
    }


    private boolean removePollable(Pollable pollable) {
        if (pollable != null) {
            int index = pollables.indexOf(pollable);
            pollables.set(index, null);
            listeners.set(index, null);
        }

        return pollable != null;
    }
}
