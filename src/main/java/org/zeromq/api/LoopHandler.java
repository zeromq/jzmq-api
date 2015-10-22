package org.zeromq.api;

/**
 * Callback from within an event-driven reactor.
 */
public interface LoopHandler {
    /**
     * Execute a loop operation.
     * 
     * @param socket The Socket
     * @param args Optional arguments
     */
    void execute(Reactor reactor, Socket socket, Object... args);
}
