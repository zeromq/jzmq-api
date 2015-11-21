package org.zeromq.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Routed message contains route frames and payload frames.
 */
public class RoutedMessage extends Message {

    /**
     * Construct an empty message.
     */
    public RoutedMessage() {

    }

    /**
     * Takes a route with no frames.
     * 
     * @param route The initial route
     */
    public RoutedMessage(Route route) {
        super(route.getRoutingFrames());
    }

    /**
     * Takes routes with no frames.
     * 
     * @param routes An initial list of routes
     */
    public RoutedMessage(List<Route> routes) {
        for (Route route : routes) {
            addFrames(route.getRoutingFrames());
        }
    }

    /**
     * Takes the existing message and adds a route to the beginning of it.
     * 
     * @param route The initial route
     * @param message A message containing frames to be added
     */
    public RoutedMessage(Route route, Message message) {
        this(route);
        addFrames(message);
    }

    /**
     * Takes the existing message and adds routes to the beginning of it.
     * 
     * @param routes An initial list of routes
     * @param message A message containing frames to be added
     */
    public RoutedMessage(List<Route> routes, Message message) {
        this(routes);
        addFrames(message);
    }

    /**
     * Get the message portion of the routed message, which is the frames after
     * all routing frames.
     * 
     * @return The message payload
     */
    public Message getPayload() {
        List<Frame> frames = getFrames();
        //each route is 2 frames, so get everything after the routing frames.
        return new Message(frames.subList(getRoutes().size() * 2, frames.size()));
    }

    public List<Route> getRoutes() {
        List<Route> results = new ArrayList<Route>();
        List<Frame> frames = getFrames();
        for (int i = 0; i < frames.size(); i++) {
            Frame address = frames.get(i);
            if (frames.size() > i + 1) {
                Frame blank = frames.get(++i);
                if (blank.isBlank()) {
                    results.add(new Route(address.getData()));
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return results;
    }

    /**
     * Mutates this Message, to remove the top-most Route, which is then returned.
     * 
     * @return The unwrapped Route
     */
    public Route unwrap() {
        if (getRoutes().isEmpty()) {
            throw new IllegalStateException("Cannot unwrap an unrouted message.");
        }
        Frame route = super.popFrame();
        super.popFrame();

        return new Route(route.getData());
    }

    /**
     * Represents a route, or address frame, which will be combined with a blank
     * frame over the wire.
     */
    public static class Route {
        public static final Frame BLANK = new Frame(new byte[0]);

        //todo store Frame or bytes?
        private final byte[] address;

        public Route(String address) {
            this(address.getBytes(Message.CHARSET));
        }

        public Route(byte[] address) {
            this.address = address;
        }

        public byte[] getAddress() {
            return address;
        }

        public String getString() {
            return new String(address, Message.CHARSET);
        }

        public List<Frame> getRoutingFrames() {
            List<Frame> frames = new ArrayList<Frame>(2);
            frames.add(new Frame(address));
            frames.add(BLANK);
            return frames;
        }

        @Override
        public String toString() {
            return "Route{address=" + new String(address, Message.CHARSET) + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Route route = (Route) o;

            if (!Arrays.equals(address, route.address)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return address != null ? Arrays.hashCode(address) : 0;
        }
    }

}