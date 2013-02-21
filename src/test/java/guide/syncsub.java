package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class syncsub {

    public static void main(String[] args) throws Exception {
        ManagedContext context = new ManagedContext();
        Socket subscriber = context.buildSocket(SocketType.SUB).asSubscribable()
                .subscribe(new byte[0])
                .connect("tcp://localhost:5561");

        Thread.sleep(1000L);
        Socket syncClient = context.buildSocket(SocketType.REQ).connect("tcp://localhost:5562");

        //send the "ready"
        syncClient.send(new byte[0]);
        //wait for the "go" message
        syncClient.receive();

        int numberOfUpdates = 0;
        while (true) {
            byte[] bytes = subscriber.receive();
            numberOfUpdates++;
            if ("END".equals(new String(bytes))) {
                break;
            }
        }
        System.out.printf("Received %d updates%n", numberOfUpdates);
        context.close();
    }
}
