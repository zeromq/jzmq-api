package org.zeromq.api;

/**
 * Callback from within an event-driven reactor.
 */
public interface LoopHandler {
    /**
     * Execute a loop operation.
     * 
     * @param reactor The Reactor
     * @param pollable The Pollable containing the socket or channel
     */
    void execute(Reactor reactor, Pollable pollable);
}
