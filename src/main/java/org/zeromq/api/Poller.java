package org.zeromq.api;

/**
 * Poller for polling sockets and receiving callbacks for events.
 */
public interface Poller {

    /**
     * Poll a socket indefinitely.
     */
    void poll();

    /**
     * Poll a socket for a given amount of time.
     * 
     * @param timeoutMillis The number of milliseconds to wait before returning
     */
    void poll(long timeoutMillis);

    /**
     * Enable a socket in the poller after it has been disabled.
     * 
     * @param socket The socket registered with the poller to be enabled
     * @return The new index of the socket in the poller, for reference
     */
    int enable(Socket socket);

    /**
     * Disable a socket in the poller, preventing it from waking up the thread
     * when messages are received on it.
     * 
     * @param socket The socket registered with the poller to be disabled
     * @return true if the socket was disabled, false otherwise
     */
    boolean disable(Socket socket);

}