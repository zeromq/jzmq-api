package guide;

import org.zeromq.api.*;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.PollerBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class lbbroker2 {
    private static final int NUMBER_OF_CLIENTS = 10;
    private static final int NUMBER_OF_WORKERS = 3;
    public static final byte[] READY = "READY".getBytes();

    /**
     * Basic request-reply client using REQ socket
     */
    private static class ClientTask implements Runnable {
        @Override
        public void run() {
            ManagedContext context = new ManagedContext();

            Socket client = GuideHelper.assignPrintableIdentity(context.buildSocket(SocketType.REQ)).connect("ipc://frontend.ipc");

            //  Send request, get reply
            client.send("HELLO".getBytes());
            String reply = new String(client.receive());
            System.out.println("Client: " + reply);

            context.close();
        }
    }

    /**
     * While this example runs in a single process, that is just to make
     * it easier to start and stop the example. Each thread has its own
     * context and conceptually acts as a separate process.
     * This is the worker task, using a REQ socket to do load-balancing.
     */
    private static class WorkerTask implements Runnable {

        private AtomicBoolean shouldStop = new AtomicBoolean(false);

        @Override
        public void run() {
            ManagedContext context = new ManagedContext();
            //  Prepare our context and sockets
            Socket worker = GuideHelper.assignPrintableIdentity(context.buildSocket(SocketType.REQ)).withReceiveTimeout(100).connect("ipc://backend.ipc");

            //  Tell backend we're ready for work
            worker.send("READY".getBytes());

            while (!shouldStop.get()) {
                RoutedMessage message = worker.receiveRoutedMessage();
                if (message == null) {
                    continue;
                }
                System.out.println("Worker: " + new String(message.getPayload().getFirstFrame().getData()));
                List<RoutedMessage.Route> routes = message.getRoutes();
                worker.send(new RoutedMessage(routes, new Message("OK".getBytes())));
            }
            context.close();
        }
    }

    /**
     * This is the main task. It starts the clients and workers, and then
     * routes requests between the two layers. Workers signal READY when
     * they start; after that we treat them as ready when they reply with
     * a response back to a client. The load-balancing data structure is
     * just a queue of next available workers.
     */
    public static void main(String[] args) {
        ManagedContext context = new ManagedContext();
        //  Prepare our context and sockets
        final Socket frontend = context.buildSocket(SocketType.ROUTER).bind("ipc://frontend.ipc");
        final Socket backend = context.buildSocket(SocketType.ROUTER).bind("ipc://backend.ipc");

        final AtomicInteger clientNumber = new AtomicInteger(0);
        for (; clientNumber.get() < NUMBER_OF_CLIENTS; clientNumber.incrementAndGet()) {
            new Thread(new ClientTask()).start();
        }

        List<WorkerTask> workerContexts = new ArrayList<WorkerTask>(NUMBER_OF_WORKERS);
        for (int i = 0; i < NUMBER_OF_WORKERS; i++) {
            WorkerTask workerTask = new WorkerTask();
            workerContexts.add(workerTask);
            new Thread(workerTask).start();
        }

        //  Queue of available workers
        final Queue<RoutedMessage.Route> workerQueue = new LinkedList<RoutedMessage.Route>();

        //  Here is the main loop for the load-balancer. It works the same way
        //  as the previous example, but is a lot shorter because the Message API gives
        //  us an API that does more with fewer calls:

        final AtomicBoolean shouldContinue = new AtomicBoolean(true);

        while (shouldContinue.get()) {
            //  Initialize poll set
            PollerBuilder pollerBuilder = context.buildPoller();
            pollerBuilder.withPollable(context.newPollable(backend, PollerType.POLL_IN), new PollAdapter() {
                @Override
                public void handleIn(Socket backEnd) {
                    RoutedMessage routedMessage = backEnd.receiveRoutedMessage();
                    RoutedMessage.Route topRoute = routedMessage.unwrap();
                    workerQueue.add(topRoute);

                    Message payload = routedMessage.getPayload();
                    byte[] clientAddress = payload.getFirstFrame().getData();
                    if (!Arrays.equals(READY, clientAddress)) {
                        frontend.send(new RoutedMessage(routedMessage.getRoutes(), payload));

                        if (clientNumber.decrementAndGet() == 0) {
                            shouldContinue.set(false);
                        }
                    }
                }
            });

            //  Poll front-end only if we have available workers
            if (workerQueue.size() > 0) {
                pollerBuilder.withPollable(context.newPollable(frontend, PollerType.POLL_IN), new PollAdapter() {
                    @Override
                    public void handleIn(Socket frontEnd) {
                        Message message = frontEnd.receiveMessage();
                        RoutedMessage.Route workerAddress = workerQueue.poll();
                        backend.send(new RoutedMessage(workerAddress, message));
                    }
                });
            }

            pollerBuilder.create().poll();
        }

        //shut down the worker pool.
        for (WorkerTask workerTask : workerContexts) {
            workerTask.shouldStop.set(true);
        }

        context.close();
    }

}
