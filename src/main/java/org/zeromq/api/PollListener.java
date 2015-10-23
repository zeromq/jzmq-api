package org.zeromq.api;

import java.nio.channels.SelectableChannel;

public interface PollListener {
    void handleIn(Socket socket);
    void handleOut(Socket socket);
    void handleError(Socket socket);

    void handleIn(SelectableChannel channel);
    void handleOut(SelectableChannel channel);
    void handleError(SelectableChannel channel);
}
