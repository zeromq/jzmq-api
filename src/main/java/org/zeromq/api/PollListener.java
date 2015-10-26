package org.zeromq.api;

public interface PollListener {
    void handleIn(Pollable pollable);
    void handleOut(Pollable pollable);
    void handleError(Pollable pollable);
}
