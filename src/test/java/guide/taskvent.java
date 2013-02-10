package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.util.Random;

public class taskvent {

    public static void main(String[] args) throws Exception {
        ManagedContext context = new ManagedContext();
        Socket sender = context.buildSocket(SocketType.PUSH).bind("tcp://*:5557");
        Socket sink = context.buildSocket(SocketType.PUSH).connect("tcp://localhost:5558");

        System.out.println("Press Enter when the workers are ready...");
        System.in.read();
        System.out.println("Sending tasks to workers...");

        //The first message signals the start of the batch.
        sink.send("0".getBytes());

        Random random = new Random(System.currentTimeMillis());

        int totalCost = 0;
        for (int i = 0; i < 100; i++) {
            int workload = random.nextInt(100) + 1;
            totalCost += workload;
            System.out.print(workload + ".");
            sender.send(String.format("%d", workload).getBytes());
        }

        System.out.println("total expected cost = " + totalCost);
        Thread.sleep(1000L); // give 0mq time to deliver the messages.
        context.close();
    }
}
