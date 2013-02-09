package org.zeromq.jzmq.sockets;

import org.zeromq.api.Bindable;
import org.zeromq.api.Connectable;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

/**
 * SocketBuilder
 */
public abstract class SocketBuilder implements Bindable, Connectable {
    private String identity;
    private long swapSize;
    private long lingerMS = 0;
    private long sendHWM;
    private long receiveHWM;
    private SocketType socketType;
    protected ManagedContext context;

    public SocketBuilder(ManagedContext context, SocketType socketType) {
        this.socketType = socketType;
        this.context = context;
    }

    /**
     * Returns the underlying socket type
     * 
     * @return socket type
     */
    public SocketType getSocketType() {
        return socketType;
    }

    /**
     * Set the linger period for the specified socket. The linger period determines how long pending which have yet to
     * sent to a peer shall linger in memory after a socket is closed.
     * 
     * @param lingerMS the linger period in millis
     * @return builder object
     */
    public SocketBuilder withLinger(long lingerMS) {
        this.lingerMS = lingerMS;
        return this;
    }

    /**
     * Returns the linger period in millis
     * 
     * @return linger period
     */
    public long getLinger() {
        return lingerMS;
    }

    /**
     * Set the socket identity of the specified socket. Socket identity determines if existing 0MQ infrastructure
     * (message queues, forwarding devices) shall be identified with a specific application and persist across multiple
     * runs of the application.
     * 
     * If the socket has no identity, each run of the application is completely independent of other runs.
     * 
     * The identity can only be between 0 and 256 bytes long exclusive.
     * 
     * @param identity the identity
     * @return builder object
     */
    public SocketBuilder withIdentity(String identity) {
        this.identity = identity;
        return this;
    }

    /**
     * Return the identity of the socket
     * 
     * @return the identity
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * The swap option shall set the disk offload (swap) size for the specified socket. A socket which has a positive
     * value may exceed it's high water mark; in the case of outstanding messages, they shall be offloaded to storage on
     * disk.
     * 
     * @param swapSize swap in bytes
     * @return builder object
     */
    public SocketBuilder withSwap(long swapSize) {
        this.swapSize = swapSize;
        return this;
    }

    /**
     * Return the swap size in bytes
     * 
     * @return the swap size
     */
    public long getSwap() {
        return swapSize;
    }

    public SocketBuilder withSendHWM(long sendHWM) {
        this.sendHWM = sendHWM;
        return this;
    }

    public long getSendHWM() {
        return sendHWM;
    }

    /**
     * The RECVHWM option shall set the high water mark (HWM) for inbound messages on the specified socket. The HWM is a
     * hard limit on the maximum number of outstanding 0MQ shall queue in memory for any single peer that the specified
     * socket is communicating with.
     * 
     * If this limit has been reached the socket shall enter an exceptional state and depending on the socket type, 0MQ
     * shall take appropriate action such as blocking or dropping sent messages. Refer to the individual socket
     * descriptions in <a href="http://api.zeromq.org/3-2:zmq-socket">zmq_socket(3)</a> for details on the exact action
     * taken for each socket type.
     * 
     * @param receiveHWM recv high water mark
     * @return builder object
     */
    public SocketBuilder withReceiveHWM(long receiveHWM) {
        this.receiveHWM = receiveHWM;
        return this;
    }

    /**
     * Returns the recv high water mark
     * 
     * @return recv high water mark
     */
    public long getRecvHWM() {
        return receiveHWM;
    }

    public abstract Socket connect(String url);

    public abstract Socket bind(String url);
}
