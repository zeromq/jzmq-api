package org.zeromq.jzmq.poll;

import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;

import java.nio.channels.SelectableChannel;
import java.util.Arrays;
import java.util.EnumSet;

public class PollableImpl implements Pollable {
    private final Socket socket;
    private final SelectableChannel channel;
    private final PollerType[] options;

    public PollableImpl(Socket socket, PollerType... options) {
        this.socket = socket;
        this.channel = null;
        this.options = options;
    }

    public PollableImpl(SelectableChannel channel, PollerType... options) {
        this.socket = null;
        this.channel = channel;
        this.options = options;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public SelectableChannel getChannel() {
        return channel;
    }

    @Override
    public EnumSet<PollerType> getOptions() {
        if (options.length == 0) {
            return EnumSet.allOf(PollerType.class);
        }
        return EnumSet.copyOf(Arrays.asList(options));
    }
}
