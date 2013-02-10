package org.zeromq.jzmq.poll;

import org.zeromq.api.Pollable;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;

import java.util.Arrays;
import java.util.EnumSet;

public class PollableImpl implements Pollable {
    private final Socket socket;
    private final PollerType[] options;

    public PollableImpl(Socket socket, PollerType... options) {
        this.socket = socket;
        this.options = options;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public EnumSet<PollerType> getOptions() {
        if (options.length == 0) {
            return EnumSet.allOf(PollerType.class);
        }
        return EnumSet.copyOf(Arrays.asList(options));
    }
}
