package org.zeromq.jzmq;

import org.zeromq.ZMQ;
import org.zeromq.api.PollListener;
import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Poller {

    private final Map<Integer, Socket> socketIndex = new HashMap<Integer, Socket>();
    private final Map<Integer, PollListener> listeners = new HashMap<Integer, PollListener>();
    private final ZMQ.Poller poller;

    public Poller(ManagedContext context, Map<Pollable, PollListener> pollables) {
        this.poller = context.newZmqPoller();
        for (Map.Entry<Pollable, PollListener> entry : pollables.entrySet()) {
            Pollable pollable = entry.getKey();

            Socket socket = pollable.getSocket();
            int options = sumOptions(pollable);
            int index = poller.register(socket.getZMQSocket(), options);
            socketIndex.put(index, socket);

            PollListener listener = entry.getValue();
            listeners.put(index, listener);
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

    public void poll() {
        poller.poll();
        for (Map.Entry<Integer, Socket> entry : socketIndex.entrySet()) {
            Integer index = entry.getKey();
            if (poller.pollin(index)) {
                listeners.get(index).handleIn(entry.getValue());
            }
            if (poller.pollout(index)) {
                listeners.get(index).handleOut(entry.getValue());
            }
            if (poller.pollerr(index)) {
                listeners.get(index).handleError(entry.getValue());
            }
        }
    }
}
