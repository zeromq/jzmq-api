package org.zeromq.api;

/**
 * An event-driven reactor.
 */
public interface Reactor {
    /**
     * Start the reactor
     */
    void start();

    /**
     * Start the reactor
     */
    void stop();

    /**
     * Add a new Pollable to this Reactor.
     * <p>
     * This method is not thread-safe, and should only be done from inside the
     * LoopHandler when invoked by the Reactor on its own thread.
     * 
     * @param pollable The Pollable with the socket to poll
     * @param handler The loop handler
     * @param args Optional arguments
     */
    void addPollable(Pollable pollable, LoopHandler handler, Object... args);

    /**
     * Add a new ReactorTimer to this Reactor.
     * <p>
     * This method is not thread-safe, and should only be done from inside the
     * LoopHandler when invoked by the Reactor on its own thread.
     * 
     * @param initialDelay The initial delay, in milliseconds
     * @param numIterations The number of iterations, after which this timer stop
     * @param handler The loop handler
     * @param args Optional arguments
     */
    void addTimer(long initialDelay, int numIterations, LoopHandler handler, Object... args);

    /**
     * Cancel an existing Pollable or ReactorTimer and remove the corresponding
     * LoopHandler from executing.
     * <p>
     * This method is not thread-safe, and should only be done from inside the
     * LoopHandler when invoked by the Reactor on its own thread.
     * 
     * @param handler The loop handler
     */
    void cancel(LoopHandler handler);
}
