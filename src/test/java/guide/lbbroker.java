package guide;

import org.zeromq.api.*;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.PollerBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class lbbroker {
    private static final int NUMBER_OF_CLIENTS = 10;
    private static final int NUMBER_OF_WORKERS = 3;
    public static final byte[] READY = "READY".getBytes();
    public static final byte[] EMPTY_FRAME = new byte[0];

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
                // client response is [client identity][empty][request]
                byte[] address = worker.receive();
                if (address == null) {
                    continue;
                }
                worker.receive();  //empty
                String request = new String(worker.receive());
                System.out.println("Worker: " + request);

                worker.send(address, MessageFlag.SEND_MORE);
                worker.send(EMPTY_FRAME, MessageFlag.SEND_MORE);
                worker.send("OK".getBytes());
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

        //  Here is the main loop for the least-recently-used queue. It has two
        //  sockets; a frontend for clients and a backend for workers. It polls
        //  the backend in all cases, and polls the frontend only when there are
        //  one or more workers ready. This is a neat way to use 0MQ's own queues
        //  to hold messages we're not ready to process yet. When we get a client
        //  reply, we pop the next available worker, and send the request to it,
        //  including the originating client identity. When a worker replies, we
        //  re-queue that worker, and we forward the reply to the original client,
        //  using the reply envelope.

        //  Queue of available workers
        final Queue<byte[]> workerQueue = new LinkedList<byte[]>();

        final AtomicBoolean shouldContinue = new AtomicBoolean(true);

        while (shouldContinue.get()) {
            //  Initialize poll set
            PollerBuilder pollerBuilder = context.buildPoller();
            pollerBuilder.withPollable(context.newPollable(backend, PollerType.POLL_IN), new PollAdapter() {
                @Override
                public void handleIn(Socket socket) {
                    byte[] workerAddress = socket.receive();
                    workerQueue.add(workerAddress);
                    socket.receive();//empty
                    byte[] clientAddress = socket.receive();
                    if (!Arrays.equals(READY, clientAddress)) {
                        socket.receive(); //empty
                        byte[] reply = backend.receive();
                        frontend.send(clientAddress, MessageFlag.SEND_MORE);
                        frontend.send(EMPTY_FRAME, MessageFlag.SEND_MORE);
                        frontend.send(reply);

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
                    public void handleIn(Socket socket) {
                        byte[] clientAddress = socket.receive();
                        socket.receive(); //empty
                        byte[] request = socket.receive();
                        byte[] workerAddress = workerQueue.poll();
                        backend.send(workerAddress, MessageFlag.SEND_MORE);
                        backend.send(EMPTY_FRAME, MessageFlag.SEND_MORE);
                        backend.send(clientAddress, MessageFlag.SEND_MORE);
                        backend.send(EMPTY_FRAME, MessageFlag.SEND_MORE);
                        backend.send(request);
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
