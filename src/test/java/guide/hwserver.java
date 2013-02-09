package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class hwserver {

    public static void main(String[] args) throws InterruptedException {
        ManagedContext context = new ManagedContext();
        Socket serverSocket = context.buildSocket(SocketType.REP).bind("tcp://*:5555");
        while (true) {
            byte[] message = serverSocket.receive();
            System.out.println("received request: " + new String(message));

            //do some "work"
            Thread.sleep(1000L);

            serverSocket.send("World!".getBytes());
        }
    }
}
