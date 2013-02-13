package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class mtserver {

    public static void main(String[] args) {
        final ManagedContext context = new ManagedContext();
        Socket clients = context.buildSocket(SocketType.ROUTER).bind("tcp://*:5555");
        Socket workers = context.buildSocket(SocketType.DEALER).bind("inproc://workers");

        for (int i = 0; i < 5; i++) {
            Runnable worker = new Worker(context);
            new Thread(worker).start();
        }

        context.proxy(clients, workers);
    }

    private static class Worker implements Runnable {
        private final ManagedContext context;

        public Worker(ManagedContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            Socket socket = context.buildSocket(SocketType.REP).connect("inproc://workers");
            while (true) {
                byte[] request = socket.receive();
                System.out.println("Received request: [" + new String(request) + "]");

                //do some "work"
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                socket.send("World".getBytes());
            }
        }
    }
}
