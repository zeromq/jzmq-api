package org.zeromq;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import org.zeromq.api.Backgroundable;
import org.zeromq.api.DeviceType;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.PollListener;
import org.zeromq.api.Poller;
import org.zeromq.api.Reactor;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.poll.PollerBuilder;
import org.zeromq.jzmq.reactor.ReactorBuilder;
import org.zeromq.jzmq.sockets.SocketBuilder;

import java.nio.channels.SelectableChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class containing utility methods for creating ØMQ Sockets and other related
 * objects without using a ØMQ Context directly.
 */
public class Sockets {
    /**
     * Protected constructor.
     */
    private Sockets() {
    }

    /**
     * Create a builder capable of constructing ØMQ Sockets of the given socket type.
     * 
     * @param socketType The socket type
     * @return A builder for constructing ØMQ Sockets
     */
    public static SocketBuilder buildSocket(SocketType socketType) {
        return ContextFactory.context().buildSocket(socketType);
    }

    /**
     * Create a ØMQ Socket and connect it to a given url.
     * 
     * @param socketType The socket type    
     * @param url The url to connect to
     * @return A ØMQ Socket
     */
    public static Socket connect(SocketType socketType, String url) {
        return buildSocket(socketType).connect(url);
    }

    /**
     * Create a ØMQ Socket and bind it to a given url.
     * 
     * @param socketType The socket type
     * @param url The url to bind to
     * @return A ØMQ Socket
     */
    public static Socket bind(SocketType socketType, String url) {
        return buildSocket(socketType).bind(url);
    }

    /**
     * Create a builder capable of constructing a ØMQ Poller.
     * 
     * @return A builder for constructing a ØMQ Poller
     */
    public static PollerBuilder buildPoller() {
        return ContextFactory.context().buildPoller();
    }

    /**
     * Create a ØMQ Poller with a single PollListener which handles incoming
     * messages from the given ØMQ Sockets.
     * 
     * @param listener A listener for handling incoming messages
     * @param sockets One or more ØMQ Sockets
     * @return A new ØMQ Poller
     */
    public static Poller newPoller(PollListener listener, Socket... sockets) {
        return newPoller(listener, asList(sockets), null);
    }

    /**
     * Create a ØMQ Poller with a single PollListener which handles incoming
     * messages from the given SelectableChannels.
     * 
     * @param listener A listener for handling incoming messages
     * @param channels One or more channels that can be multiplexed with ØMQ Sockets
     * @return A new ØMQ Poller
     */
    public static Poller newPoller(PollListener listener, SelectableChannel... channels) {
        return newPoller(listener, null, asList(channels));
    }

    /**
     * Create a ØMQ Poller with a single PollListener which handles incoming
     * messages from the given ØMQ Socket and SelectableChannel.
     * 
     * @param listener A listener for handling incoming messages
     * @param socket A ØMQ Socket
     * @param channel A selectable channel that can be multiplexed with ØMQ Sockets
     * @return A new ØMQ Poller
     */
    public static Poller newPoller(PollListener listener, Socket socket, SelectableChannel channel) {
        return newPoller(listener, singletonList(socket), singletonList(channel));
    }

    /**
     * Create a ØMQ Poller with a single PollListener which handles incoming
     * messages from the given ØMQ Sockets and SelectableChannels.
     * 
     * @param listener A listener for handling incoming messages
     * @param sockets One or more ØMQ Sockets
     * @param channels One or more channels that can be multiplexed with ØMQ Sockets
     * @return A new ØMQ Poller
     */
    public static Poller newPoller(PollListener listener, List<Socket> sockets, List<SelectableChannel> channels) {
        PollerBuilder builder = buildPoller();
        if (sockets != null) {
            for (Socket socket : sockets) {
                builder.withInPollable(socket, listener);
            }
        }
        if (channels != null) {
            for (SelectableChannel channel : channels) {
                builder.withInPollable(channel, listener);
            }
        }

        return builder.build();
    }

    /**
     * Create a builder capable of constructing a ØMQ Reactor.
     * 
     * @return A builder for constructing a ØMQ Reactor
     */
    public static ReactorBuilder buildReactor() {
        return ContextFactory.context().buildReactor();
    }

    /**
     * Create a ØMQ Reactor with a single LoopHandler which handles incoming
     * messages from the given ØMQ Sockets.
     * 
     * @param handler A handler for executing event-driven polling of Sockets
     * @param sockets One or more ØMQ Sockets
     * @return A new ØMQ Reactor
     */
    public static Reactor newReactor(LoopHandler handler, Socket... sockets) {
        return newReactor(handler, asList(sockets), null);
    }

    /**
     * Create a ØMQ Reactor with a single LoopHandler which handles incoming
     * messages from the given SelectableChannels.
     * 
     * @param handler A handler for executing event-driven polling of Sockets
     * @param channels One or more channels that can be multiplexed with ØMQ Sockets
     * @return A new ØMQ Reactor
     */
    public static Reactor newReactor(LoopHandler handler, SelectableChannel... channels) {
        return newReactor(handler, null, asList(channels));
    }

    /**
     * Create a ØMQ Reactor with a single LoopHandler which handles incoming
     * messages from the given ØMQ Socket and SelectableChannel.
     * 
     * @param handler A handler for executing event-driven polling of Sockets
     * @param socket A ØMQ Socket
     * @param channel A selectable channel that can be multiplexed with ØMQ Sockets
     * @return A new ØMQ Reactor
     */
    public static Reactor newReactor(LoopHandler handler, Socket socket, SelectableChannel channel) {
        return newReactor(handler, singletonList(socket), singletonList(channel));
    }

    /**
     * Create a ØMQ Reactor with a single LoopHandler which initially handles
     * a repeating timer with the given interval.
     *
     * @param handler A handler for executing event-driven polling of Sockets
     * @param interval The initial and subsequet delay for a repeating timer event
     * @param unit The time unit for the timer interval
     * @return A new ØMQ Reactor
     */
    public static Reactor newReactor(LoopHandler handler, long interval, TimeUnit unit) {
        return ContextFactory.context().buildReactor()
            .withTimerRepeating(interval, unit, handler)
            .build();
    }

    /**
     * Create a ØMQ Reactor with a single LoopHandler which handles incoming
     * messages from the given ØMQ Sockets and SelectableChannels.
     * 
     * @param handler A handler for executing event-driven polling of Sockets
     * @param sockets One or more ØMQ Sockets
     * @param channels One or more channels that can be multiplexed with ØMQ Sockets
     * @return A new ØMQ Poller
     */
    public static Reactor newReactor(LoopHandler handler, List<Socket> sockets, List<SelectableChannel> channels) {
        ReactorBuilder builder = buildReactor();
        if (sockets != null) {
            for (Socket socket : sockets) {
                builder.withInPollable(socket, handler);
            }
        }
        if (channels != null) {
            for (SelectableChannel channel : channels) {
                builder.withInPollable(channel, handler);
            }
        }

        return builder.build();
    }

    /**
     * Start a ØMQ Device of the given type, which will run in the background to
     * bridge two networks together.
     * 
     * @param deviceType The device type, specifying the pattern to use
     */
    public static void start(DeviceType deviceType, String frontendUrl, String backendUrl) {
        ContextFactory.context().buildDevice(deviceType)
            .withFrontendUrl(frontendUrl)
            .withBackendUrl(backendUrl)
            .start();
    }

    /**
     * Create a ØMQ proxy and start it up.  Returns when the context is closed.
     * 
     * @param frontEnd The front-end socket which will be proxied to/from the back-end
     * @param backEnd The back-end socket which will be proxied to/from the front-end
     */
    public static void proxy(Socket frontEnd, Socket backEnd) {
        ContextFactory.context().proxy(frontEnd, backEnd);
    }

    /**
     * Create a ØMQ proxy and start it up on another thread that exits when the
     * context is closed.
     * 
     * @param frontEnd The front-end socket which will be proxied to/from the back-end
     * @param backEnd The back-end socket which will be proxied to/from the front-end
     */
    public static void forward(Socket frontEnd, Socket backEnd) {
        ContextFactory.context().forward(frontEnd, backEnd);
    }

    /**
     * Run a background thread with an inproc PAIR socket for communication.
     * 
     * @param backgroundable The task to be performed on the background thread
     * @param args Optional arguments for the task
     * @return The inproc PAIR socket for communicating with the background thread
     */
    public static Socket fork(Backgroundable backgroundable, Object... args) {
        return ContextFactory.context().fork(backgroundable, args);
    }
}
