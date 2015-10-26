package org.zeromq.api;

import java.net.InetAddress;

/**
 * Listener to handle beacon events sent via UDP.
 */
public interface BeaconListener {
    /**
     * Handle a validated beacon event.
     * 
     * @param sender The sender of the beacon
     * @param beacon The beacon, containing connection information
     */
    void onBeacon(InetAddress sender, UdpBeacon beacon);
}
