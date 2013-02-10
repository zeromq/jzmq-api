package org.zeromq.api;

public interface PollListener {
    void handleIn(Socket socket);
    void handleOut(Socket socket);
    void handleError(Socket socket);
}
