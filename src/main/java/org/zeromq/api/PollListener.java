package org.zeromq.api;

/**
 * A listener of a Poller.
 */
public interface PollListener {
    /**
     * Callback to handle incoming messages.
     * 
     * @param pollable The pollable
     */
    void handleIn(Pollable pollable);

    /**
     * Callback to handle outgoing messages.
     * 
     * @param pollable The pollable
     */
    void handleOut(Pollable pollable);

    /**
     * Callback to handle errors.
     * 
     * @param pollable The pollable
     */
    void handleError(Pollable pollable);
}
