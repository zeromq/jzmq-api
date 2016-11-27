package org.zeromq.api;

import org.zeromq.jzmq.beacon.BeaconReactorBuilder;
import org.zeromq.jzmq.bstar.BinaryStarReactorBuilder;
import org.zeromq.jzmq.bstar.BinaryStarSocketBuilder;
import org.zeromq.jzmq.device.DeviceBuilder;
import org.zeromq.jzmq.poll.PollerBuilder;
import org.zeromq.jzmq.reactor.ReactorBuilder;
import org.zeromq.jzmq.sockets.SocketBuilder;

import java.io.Closeable;
import java.nio.channels.SelectableChannel;

/**
 * A ØMQ context is thread safe and may be shared among as many application threads as necessary, without any additional
 * locking required on the part of the caller.
 */
public interface Context extends Closeable {
    /**
     * Create a ØMQ Socket of type SocketType
     * 
     * @param type socket type
     * @return A builder for constructing and connecting ØMQ Sockets
     */
    SocketBuilder buildSocket(SocketType type);

    /**
     * @return The ØMQ version, in a pretty-printed String.
     */
    String getVersionString();

    /**
     * @return The ØMQ version, in integer form.
     */
    int getFullVersion();

    /**
     * Create a new Poller, which will allow callback-based polling of Sockets.
     * 
     * @return A builder for constructing ØMQ Pollers
     * @see Pollable
     * @see PollerType
     */
    PollerBuilder buildPoller();

    /**
     * Create a new Reactor, which will allow event-driven and timer-based
     * polling of Sockets.
     * 
     * @return A builder for constructing a Reactor
     */
    ReactorBuilder buildReactor();

    /**
     * Create a new BinaryStarReactor, which will create one half of an HA-pair
     * with event-driven polling of a client Socket.
     * 
     * @return A builder for constructing a BinaryStarReactor
     */
    BinaryStarReactorBuilder buildBinaryStarReactor();

    /**
     * Create a ØMQ Socket, backed by a background agent that is connecting
     * to a BinaryStarReactor HA-pair.
     *
     * @return A builder for constructing connecting a BinaryStarReactor client Socket
     */
    BinaryStarSocketBuilder buildBinaryStarSocket();

    /**
     * Create a new BeaconReactor, which will send and receive UDP beacons
     * on a broadcast address, with event-driven handling of received beacons.
     * 
     * @return A builder for constructing a BeaconReactor
     */
    BeaconReactorBuilder buildBeaconReactor();

    /**
     * Create a ØMQ Device of type DeviceType, which will bridge two networks
     * together using patterns for specific SocketTypes.
     *
     * @param deviceType The device type, specifying the pattern to use
     * @return A builder for constructing ØMQ Devices
     */
    DeviceBuilder buildDevice(DeviceType deviceType);

    /**
     * Create a new Pollable from the socket, with the requested options.
     * 
     * @param socket A socket to wrap for polling
     * @param options Polling options (IN, OUT, ERROR)
     * @return A new Pollable, for use with a Poller.
     */
    Pollable newPollable(Socket socket, PollerType... options);

    /**
     * Create a new Pollable from the socket, with the requested options.
     *
     * @param channel A channel to wrap for polling
     * @param options Polling options (IN, OUT, ERROR)
     * @return A new Pollable, for use with a Poller.
     */
    Pollable newPollable(SelectableChannel channel, PollerType... options);

    /**
     * Create a ØMQ proxy and start it up.  Returns when the context is closed.
     * 
     * @param frontEnd The front-end socket which will be proxied to/from the back-end
     * @param backEnd The back-end socket which will be proxied to/from the front-end
     */
    void proxy(Socket frontEnd, Socket backEnd);

    /**
     * Create a ØMQ proxy and start it up on another thread that exits when the
     * context is closed.
     *
     * @param frontEnd The front-end socket which will be proxied to/from the back-end
     * @param backEnd The back-end socket which will be proxied to/from the front-end
     */
    void forward(Socket frontEnd, Socket backEnd);

    /**
     * Alias of {@link #forward}.
     *
     * @param frontEnd The front-end socket which will be proxied to/from the back-end
     * @param backEnd The back-end socket which will be proxied to/from the front-end
     */
    void queue(Socket frontEnd, Socket backEnd);

    /**
     * Run a background thread with an inproc PAIR socket for communication.
     * 
     * @param backgroundable The task to be performed on the background thread
     * @return The inproc PAIR socket for communicating with the background thread
     */
    Socket fork(Backgroundable backgroundable);

    /**
     * Run a background thread using the given socket for communication.
     * 
     * @param socket The socket owned by the background thread
     * @param backgroundable The task to be performed on the background thread
     */
    void fork(Socket socket, Backgroundable backgroundable);

    /**
     * Create a new Context with the same underlying ØMQ context, with an empty
     * (separate) list of managed Sockets and Backgroundables.
     * <p>
     * The returned context will not attempt to terminate the underlying ØMQ
     * context when closed, and will only close managed sockets, etc.
     * 
     * @return A new context with the same underlying ØMQ context
     */
    Context shadow();

    /**
     * Close the context and any open sockets.
     */
    void close();

    /**
     * Asynchronously terminate the context without closing any open sockets,
     * forcing pollers and waiters to abort.
     */
    void terminate();
}
