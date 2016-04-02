package org.zeromq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.Context;
import org.zeromq.jzmq.ManagedContext;

/**
 * Factory containing utility methods for managing instances of a ØMQ Context.
 */
public class ContextFactory {

    /** Default io threads, to be customized by the application, or set via property. */
    public static int DEFAULT_IO_THREADS = Integer.parseInt(
        System.getProperty("zmq.default.io.threads", "1"));

    private static final Logger log = LoggerFactory.getLogger(ContextFactory.class);

    /**
     * Protected constructor.
     */
    private ContextFactory() {
    }

    /**
     * Create a new ØMQ Context.
     * 
     * @param ioThreads The number of background I/O threads to use
     * @return A new ØMQ Context
     */
    public static Context createContext(int ioThreads) {
        if (ioThreads < 0) {
            throw new IllegalArgumentException("ioThreads must be positive");
        }
        return new ManagedContext(ZMQ.context(ioThreads));
    }

    /**
     * Retrieve the singleton instance of a ØMQ Context.
     * 
     * @return A singleton ØMQ Context
     */
    public static Context context() {
        return ContextHolder.INSTANCE;
    }

    /**
     * Lazily initialized singleton.
     */
    private static class ContextHolder {
        private static Context INSTANCE = createContext(DEFAULT_IO_THREADS);
        static {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    log.debug("closing singleton context...");
                    INSTANCE.close();
                }
            });
        }
    }
}
