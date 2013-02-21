package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class syncpub {

    private static int NUMBER_OF_SUBSCRIBERS = 5;

    public static void main(String[] args) throws Exception {
        ManagedContext context = new ManagedContext();

        Socket publisher = context.buildSocket(SocketType.PUB).withLinger(5000L).bind("tcp://*:5561");
        Socket syncSocket = context.buildSocket(SocketType.REP).bind("tcp://*:5562");

        for (int i = 0; i < NUMBER_OF_SUBSCRIBERS; i++) {
            syncSocket.receive();
            //send the "start" message
            syncSocket.send(new byte[0]);
        }

        for (int i = 0; i < 1000000; i++) {
            publisher.send("Rhubarb".getBytes());
        }

        publisher.send("END".getBytes());

        //let the "END" message propagate.
        Thread.sleep(1000);

        context.close();
    }
}
