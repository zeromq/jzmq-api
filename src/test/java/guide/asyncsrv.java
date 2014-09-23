package guide;

import org.zeromq.api.*;
import org.zeromq.jzmq.ManagedContext;

import java.util.List;
import java.util.Random;

public class asyncsrv {
    private static final Random random = new Random(System.currentTimeMillis());


    private static class ClientTask implements Runnable {

        private final int id;

        private ClientTask(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            ManagedContext context = new ManagedContext();
            final String identity = "client-" + id;
            Socket client = context.buildSocket(SocketType.DEALER).withIdentity(identity.getBytes()).connect("tcp://localhost:5555");

            Poller poller = context.buildPoller().withPollable(context.newPollable(client, PollerType.POLL_IN), new PollAdapter() {
                @Override
                public void handleIn(Socket client) {
                    byte[] message = client.receive();
                    System.out.printf("Client %s received %s%n", identity, new String(message));
                }
            }).create();

            int requestNumber = 0;
            while (true) {
                //  Tick one hundred times per second, pulling in arriving messages
                for (int i = 0; i < 100; i++) {
                    poller.poll(10);
                }
                System.out.printf("Client %d sent Request #%d%n", id, ++requestNumber);
                client.send(("request " + requestNumber).getBytes());
            }
        }
    }

    private static class ServerWorker implements Runnable {
        private final Context context;
        private final int id;

        private ServerWorker(Context context, int id) {
            this.context = context;
            this.id = id;
        }

        @Override
        public void run() {
            Socket worker = context.buildSocket(SocketType.DEALER).connect("inproc://backend");
            System.out.println("Server worker " + id + " started");
            while (true) {
                Message message = worker.receiveMessage();
                List<Message.Frame> frames = message.getFrames();
                byte[] id = frames.get(0).getData();
                byte[] payload = frames.get(1).getData();
                System.out.printf("Server worker %s received %s from %s%n", this.id, new String(payload), new String(id));
                for (int i = 0; i < random.nextInt(5); i++) {
                    try {
                        Thread.sleep(1000 / (1 + random.nextInt(8)));
                        worker.send(message);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    private static class ServerTask implements Runnable {
        @Override
        public void run() {
            ManagedContext context = new ManagedContext();
            final Socket frontEnd = context.buildSocket(SocketType.ROUTER).bind("tcp://*:5555");
            final Socket backEnd = context.buildSocket(SocketType.DEALER).bind("inproc://backend");

            for (int i = 0; i < 5; i++) {
                new Thread(new ServerWorker(context, i)).start();
            }

            Poller poller = context.buildPoller()
                    .withPollable(context.newPollable(frontEnd, PollerType.POLL_IN), new PollAdapter() {
                        @Override
                        public void handleIn(Socket frontEnd) {
                            Message message = frontEnd.receiveMessage();
                            List<Message.Frame> frames = message.getFrames();
                            System.out.printf("Server received %s from id %s%n", new String(frames.get(1).getData()), new String(frames.get(0).getData()));
                            backEnd.send(message);
                        }
                    })
                    .withPollable(context.newPollable(backEnd, PollerType.POLL_IN), new PollAdapter() {
                        @Override
                        public void handleIn(Socket backEnd) {
                            Message message = backEnd.receiveMessage();
                            List<Message.Frame> frames = message.getFrames();
                            System.out.printf("Sending to frontEnd %s id %s%n", new String(frames.get(1).getData()), new String(frames.get(0).getData()));
                            frontEnd.send(message);
                        }
                    })
                    .create();

            while (true) {
                poller.poll();
            }

        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(new ClientTask(i)).start();
        }
        new Thread(new ServerTask()).start();
    }
}
