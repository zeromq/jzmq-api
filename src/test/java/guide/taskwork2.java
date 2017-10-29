package guide;

import org.zeromq.api.PollAdapter;
import org.zeromq.api.Poller;
import org.zeromq.api.PollerType;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.util.concurrent.atomic.AtomicBoolean;

public class taskwork2 {
    public static void main(String[] args) {
        ManagedContext context = new ManagedContext();
        Socket receiver = context.buildSocket(SocketType.PULL).connect("tcp://localhost:5557");
        final Socket sender = context.buildSocket(SocketType.PUSH).connect("tcp://localhost:5558");
        Socket controller = context.buildSocket(SocketType.SUB)
                .asSubscribable().subscribe(new byte[0])
                .connect("tcp://localhost:5559");

        final AtomicBoolean shouldStop = new AtomicBoolean(false);
        Poller poller = context.buildPoller()
                .withPollable(context.newPollable(receiver, PollerType.POLL_IN), new Worker(sender))
                .withPollable(context.newPollable(controller, PollerType.POLL_IN), new Stopper(shouldStop))
                .build();

        while (!shouldStop.get()) {
            poller.poll();
        }
    }

    private static class Worker extends PollAdapter {
        private final Socket sender;

        public Worker(Socket sender) {
            this.sender = sender;
        }

        @Override
        public void handleIn(Socket socket) {
            byte[] work = socket.receive();
            try {
                Thread.sleep(Long.valueOf(new String(work)));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sender.send("".getBytes());
            System.out.print(".");
        }
    }

    private static class Stopper extends PollAdapter {
        private final AtomicBoolean shouldStop;

        public Stopper(AtomicBoolean shouldStop) {
            this.shouldStop = shouldStop;
        }

        @Override
        public void handleIn(Socket socket) {
            socket.receive();
            shouldStop.set(true);
        }
    }
}
