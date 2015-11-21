package org.zeromq.api;

import java.nio.channels.SelectableChannel;
import java.util.EnumSet;

/**
 * An item in a poll set.
 */
public interface Pollable {
    /**
     * Get the socket (if any) for this poll item.
     * 
     * @return The socket, or null if this is a SelectableChannel Pollable
     */
    Socket getSocket();

    /**
     * Get the SelectableChannel (if any) for this poll item.
     * 
     * @return The channel, or null if this is a Socket Pollable
     */
    SelectableChannel getChannel();

    /**
     * Get the options used for this poll item
     * 
     * @return The options used for this Pollable
     */
    EnumSet<PollerType> getOptions();
}
