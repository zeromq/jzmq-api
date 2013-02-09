package org.zeromq;

import org.zeromq.api.Context;
import org.zeromq.jzmq.ManagedContext;

public class ContextFactory {
    private ContextFactory() {
    }

    public static Context createContext(int ioThreads) {
        if (ioThreads < 0) {
            throw new IllegalArgumentException("ioThreads must be positive");
        }
        return new ManagedContext(ZMQ.context(ioThreads));
    }
}
