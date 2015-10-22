package org.zeromq.jzmq.reactor;

import org.zeromq.api.LoopHandler;
import org.zeromq.api.Reactor;

class ReactorTimer implements Comparable<ReactorTimer> {
    public long initialDelay;
    public int numIterations;
    public LoopHandler handler;
    public Object[] args;

    public long nextFireTime = -1;

    public ReactorTimer(long initialDelay, int numIterations, LoopHandler handler, Object... args) {
        this.initialDelay = initialDelay;
        this.numIterations = numIterations;
        this.handler = handler;
        this.args = args;
    }

    public void recalculate(long now) {
        nextFireTime = now + initialDelay;
    }

    public void execute(Reactor reactor) {
        handler.execute(reactor, null, args);

        // Decrement counter if applicable
        if (numIterations > 0) {
            numIterations--;
        }
    }

    @Override
    public int compareTo(ReactorTimer other) {
        int result = 0;
        if (nextFireTime < other.nextFireTime) {
            result = -1;
        } else if (nextFireTime > other.nextFireTime) {
            result = 1;
        }

        return result;
    }
}