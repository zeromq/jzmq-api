package org.zeromq.api;

import java.nio.channels.SelectableChannel;

public class PollAdapter implements PollListener {

    @Override
    public void handleIn(Socket socket) {
    }

    @Override
    public void handleOut(Socket socket) {
    }

    @Override
    public void handleError(Socket socket) {
    }

    @Override
    public void handleIn(SelectableChannel channel) {
    }

    @Override
    public void handleOut(SelectableChannel channel) {
    }

    @Override
    public void handleError(SelectableChannel channel) {
    }
}
