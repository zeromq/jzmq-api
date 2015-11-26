package org.zeromq.api;

import java.nio.channels.SelectableChannel;

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

    /**
     * Enable a channel in the poller after it has been disabled.
     *
     * @param channel The channel registered with the poller to be enabled
     * @return The new index of the socket in the poller, for reference
     */
    int enable(SelectableChannel channel);

    /**
     * Disable a channel in the poller, preventing it from waking up the thread
     * when messages are received on it.
     *
     * @param channel The channel registered with the poller to be disabled
     * @return true if the socket was disabled, false otherwise
     */
    boolean disable(SelectableChannel channel);

    /**
     * Register a new poll item.
     *
     * @param pollable The pollable containing the socket and polling options
     * @param listener The listener that handles events for the given pollable
     * @return The new index of the socket in the poller, for reference
     */
    int register(Pollable pollable, PollListener listener);

    /**
     * Unregister a socket from the poller.
     *
     * @param socket The socket registered with the poller to be unregistered
     * @return true if the socket was unregistered, false otherwise
     */
    boolean unregister(Socket socket);

    /**
     * Unregister a channel from the poller.
     *
     * @param channel The channel registered with the poller to be unregistered
     * @return true if the socket was disabled, false otherwise
     */
    boolean unregister(SelectableChannel channel);

}