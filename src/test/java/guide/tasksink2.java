package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

public class tasksink2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        ManagedContext context = new ManagedContext();
        Socket receiver = context.buildSocket(SocketType.PULL).bind("tcp://*:5558");
        Socket controller = context.buildSocket(SocketType.PUB).bind("tcp://*:5559");

        //wait for the batch to start
        receiver.receive();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            String result = new String(receiver.receive());
            if (i % 10 == 0) {
                System.out.print(":");
            } else {
                System.out.print(".");
            }

        }
        long endTime = System.currentTimeMillis();

        System.out.println("\ntotal time: " + (endTime - startTime) + " ms");

        controller.send("KILL".getBytes());
        Thread.sleep(1000); // give zeromq a chance to send the last message.
        context.close();
    }
}
