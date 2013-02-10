package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

public class tasksink {
    public static void main(String[] args) throws IOException {
        ManagedContext context = new ManagedContext();
        Socket sink = context.buildSocket(SocketType.PULL).bind("tcp://*:5558");

        //wait for the start of a batch...
        sink.receive();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            String result = new String(sink.receive());
            if (i % 10 == 0) {
                System.out.print(":");
            } else {
                System.out.print(".");
            }

        }
        long endTime = System.currentTimeMillis();

        System.out.println("\ntotal time: " + (endTime - startTime) + " ms");

        context.close();
    }
}
