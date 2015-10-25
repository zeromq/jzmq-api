package org.zeromq.api;

import java.nio.channels.SelectableChannel;

public class PollAdapter implements PollListener {
    @Override
    public void handleIn(Pollable pollable) {
        handleIn(pollable.getSocket());
        handleIn(pollable.getChannel());
    }

    @Override
    public void handleOut(Pollable pollable) {
        handleOut(pollable.getSocket());
        handleOut(pollable.getChannel());
    }

    @Override
    public void handleError(Pollable pollable) {
        handleError(pollable.getSocket());
        handleError(pollable.getChannel());
    }

    protected void handleIn(Socket socket) {
    }

    protected void handleOut(Socket socket) {
    }

    protected void handleError(Socket socket) {
    }

    protected void handleIn(SelectableChannel channel) {
    }

    protected void handleOut(SelectableChannel channel) {
    }

    protected void handleError(SelectableChannel channel) {
    }
}
