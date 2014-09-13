package org.zeromq.jzmq;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.api.PollListener;
import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;
import org.zeromq.api.exception.ZMQExceptions;

public class Poller {

    private final Map<Pollable, PollListener> pollables;
    private final ZMQ.Poller poller;

    public Poller(ManagedContext context, Map<Pollable, PollListener> pollables) {
        this.poller = context.newZmqPoller(pollables.size());
        this.pollables = new LinkedHashMap<Pollable, PollListener>(pollables);
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

    public void poll(long timeoutMillis) {
        int numberOfObjects;
        try {
            numberOfObjects = poller.poll(timeoutMillis);
            if (numberOfObjects == 0) {
                return;
            }
        } catch (ZMQException ex) {
            throw ZMQExceptions.wrap(ex);
        }

        int index = 0;
        for (Map.Entry<Pollable, PollListener> entry : pollables.entrySet()) {
            Socket socket = entry.getKey().getSocket();
            PollListener listener = entry.getValue();
            if (poller.pollin(index))
                listener.handleIn(socket);
            if (poller.pollout(index))
                listener.handleOut(socket);
            if (poller.pollerr(index))
                listener.handleError(socket);

            index++;
        }

    }

    public void poll() {
        poll(-1);
    }

    public int enable(Socket socket) {
        int result = -1;
        Pollable pollable = pollable(socket);
        if (pollable != null) {
            result = register(pollable);
        }
        return result;
    }

    public boolean disable(Socket socket) {
        boolean result = false;
        Pollable pollable = pollable(socket);
        if (pollable != null) {
            poller.unregister(socket.getZMQSocket());
            result = true;
        }
        return result;
    }

    private int register(Pollable pollable) {
        return poller.register(pollable.getSocket().getZMQSocket(), sumOptions(pollable));
    }

    private Pollable pollable(Socket socket) {
        Pollable result = null;
        for (Pollable pollable : pollables.keySet()) {
            if (pollable.getSocket() == socket) {
                result = pollable;
                break;
            }
        }
        // re-add to end of list (for index-based ZMQ.Poller)
        if (result != null) {
            pollables.put(result, pollables.remove(result));
        }
        return result;
    }
}
