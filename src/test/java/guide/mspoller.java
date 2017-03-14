package guide;

import org.zeromq.api.PollAdapter;
import org.zeromq.api.Poller;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class mspoller {
    public static void main(String[] args) {
        ManagedContext context = new ManagedContext();

        //connect to the task ventilator
        final Socket receiver = context.buildSocket(SocketType.PULL).connect("tcp://localhost:5557");

        //connect to the weather server
        final Socket subscriber = context.buildSocket(SocketType.SUB).asSubscribable()
                .subscribe("10001 ".getBytes())
                .connect("tcp://localhost:5556");

        Poller poller = context.buildPoller()
                .withPollable(context.newPollable(receiver, PollerType.POLL_IN), new PollAdapter() {
                    @Override
                    public void handleIn(Socket socket) {
                        byte[] received = receiver.receive();
                        System.out.println(new String(received));
                    }
                })
                .withPollable(context.newPollable(subscriber, PollerType.POLL_IN), new PollAdapter() {
                    @Override
                    public void handleIn(Socket socket) {
                        byte[] subscribed = subscriber.receive();
                        System.out.println(new String(subscribed));
                    }
                })
                .build();

        while (true) {
            poller.poll();
        }
    }
}
