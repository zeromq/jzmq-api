package org.zeromq.jzmq.reactor;

import java.util.Arrays;
import java.util.Objects;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.Reactor;

class ReactorTimer implements Comparable<ReactorTimer> {
    public long initialDelay;
    public int numIterations;
    public LoopHandler handler;

    public long nextFireTime = -1;

    public ReactorTimer(long initialDelay, int numIterations, LoopHandler handler) {
        this.initialDelay = initialDelay;
        this.numIterations = numIterations;
        this.handler = handler;
    }

    public void recalculate(long now) {
        nextFireTime = now + initialDelay;
    }

    public void execute(Reactor reactor) {
        handler.execute(reactor, null);

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

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReactorTimer other = (ReactorTimer) obj;
        if (this.initialDelay != other.initialDelay) {
            return false;
        }
        if (this.numIterations != other.numIterations) {
            return false;
        }
        if (this.nextFireTime != other.nextFireTime) {
            return false;
        }
        if (!Objects.equals(this.handler, other.handler)) {
            return false;
        }
        return true;
    }
    
    
}