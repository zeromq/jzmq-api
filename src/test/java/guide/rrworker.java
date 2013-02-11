package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class rrworker {
    public static void main(String[] args) throws InterruptedException {
        ManagedContext context = new ManagedContext();
        Socket responder = context.buildSocket(SocketType.REP).connect("tcp://localhost:5560");

        while (true) {
            byte[] request = responder.receive();
            System.out.printf("Received request: [%s]%n", new String(request));

            Thread.sleep(1000);
            responder.send("World".getBytes());
        }

        //can't get here
        //context.close();
    }
}
