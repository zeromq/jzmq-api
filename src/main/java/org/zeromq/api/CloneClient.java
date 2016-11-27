package org.zeromq.api;

import java.util.Map;

/**
 * An API client implementing the CHP (Clustered Hashmap Protocol), which will
 * communicate via an agent (background thread) with a pair of stateful servers
 * using the Binary Star Pattern.
 */
public interface CloneClient {
    /**
     * Specify subtree for snapshot and updates.
     * <p>
     * Note: We must do this before connecting to a server as the subtree
     * specification is sent as the first command to the server.
     * 
     * @param subtree The prefix (subtree) to subscribe to
     */
    void subscribe(String subtree);

    /**
     * Connect to a new server endpoint. We can connect to at most two servers.
     * 
     * @param address The address of the server to connect to
     * @param port The port on the server to connect to
     */
    void connect(String address, int port);

    /**
     * Set a new value in the shared hashmap, with no ttl.
     *
     * @param key The key associated with the new value
     * @param value The new value to set in the shared hashmap
     */
    void set(String key, String value);

    /**
     * Set a new value in the shared hashmap.
     * 
     * @param key The key associated with the new value
     * @param value The new value to set in the shared hashmap
     * @param ttl The time-to-live for the given value, in seconds
     */
    void set(String key, String value, long ttl);

    /**
     * Look up the value in the shared hashmap.
     * 
     * @param key The key to look up
     * @return The value associated with the given key, or null
     */
    String get(String key);

    /**
     * Look up all values in the shared hashmap.
     * 
     * @return The entire map
     */
    Map<String, String> getAll();

    /**
     * Destroy this client.
     */
    void close();
}
