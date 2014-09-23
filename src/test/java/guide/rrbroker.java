package guide;

import org.zeromq.api.*;
import org.zeromq.jzmq.ManagedContext;

public class rrbroker {
    public static void main(String[] args) {
        ManagedContext context = new ManagedContext();
        final Socket frontEnd = context.buildSocket(SocketType.ROUTER).bind("tcp://*:5559");
        final Socket backEnd = context.buildSocket(SocketType.DEALER).bind("tcp://*:5560");
        while (true) {
            Poller poller = context.buildPoller()
                    .withPollable(context.newPollable(frontEnd, PollerType.POLL_IN), new Forwarder(backEnd))
                    .withPollable(context.newPollable(backEnd, PollerType.POLL_IN), new Forwarder(frontEnd))
                    .create();

            poller.poll();
        }

        //can't get here
        //context.close();
    }

    private static class Forwarder extends PollAdapter {
        private final Socket destination;

        public Forwarder(Socket destination) {
            this.destination = destination;
        }

        @Override
        public void handleIn(Socket source) {
            boolean done = false;
            while (!done) {
                byte[] bytes = source.receive();
                done = !source.hasMoreToReceive();
                destination.send(bytes, done ? MessageFlag.NONE : MessageFlag.SEND_MORE);
            }
        }
    }
}
