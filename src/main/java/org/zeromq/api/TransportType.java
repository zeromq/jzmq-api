package org.zeromq.api;

/**
 * A ØMQ socket can use multiple different underlying transport mechanisms. Each transport mechanism is suited to a
 * particular purpose and has its own advantages and drawbacks.
 */
public enum TransportType {
    /**
     * ØMQ unicast transport using TCP
     */
    TCP,
    /**
     * ØMQ reliable multicast transport using PGM
     */
    PGM,
    /**
     * ØMQ local inter-process communication transport
     */
    IPC,
    /**
     * ØMQ local in-process (inter-thread) communication transport
     */
    INPROC
}
