package guide;

import org.zeromq.api.Context;
import org.zeromq.api.MessageFlag;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;
import java.util.Random;

public class rtreq {
    public static final int NUMBER_OF_WORKERS = 10;
    private static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws IOException {
        ManagedContext context = new ManagedContext();
        Socket broker = context.buildSocket(SocketType.ROUTER).bind("tcp://*:5671");

        for (int i = 0; i < 10; i++) {
            new Thread(new Worker()).start();
        }

        //run for 5 seconds, then tell the workers they are fired.
        long endTime = System.currentTimeMillis() + 5000L;

        int workersFired = 0;
        while (true) {
            byte[] identity = broker.receive();
            broker.receive(); // envelope delimiter
            broker.receive(); // response from worker
            broker.send(identity, MessageFlag.SEND_MORE);
            broker.send(new byte[0], MessageFlag.SEND_MORE);
            if (endTime > System.currentTimeMillis()) {
                broker.send("Work harder".getBytes());
            }
            else {
                broker.send("Fired!".getBytes());
                if (++workersFired == NUMBER_OF_WORKERS) {
                    break;
                }
            }
        }

        context.close();
    }

    private static class Worker implements Runnable {
        @Override
        public void run() {
            Context context = new ManagedContext();
            Socket worker = GuideHelper.assignPrintableIdentity(context.buildSocket(SocketType.REQ))
                    .connect("tcp://localhost:5671");
            int total = 0;
            while (true) {
                worker.send("Hi Boss".getBytes());
                byte[] workload = worker.receive();
//                System.out.printf("[%s] Got '%s' from the boss.%n", Thread.currentThread().getName(), new String(workload));
                if ("Fired!".equals(new String(workload))) {
                    break;
                }
                total++;

                //do some random work
                try {
                    Thread.sleep(random.nextInt(500));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.printf("[%s] total work requests received = %d%n", Thread.currentThread().getName(), total);
            try {
                context.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
