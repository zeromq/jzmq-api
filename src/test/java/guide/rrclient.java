package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

public class rrclient {

    public static void main(String[] args) throws IOException {
        ManagedContext context = new ManagedContext();
        Socket socket = context.buildSocket(SocketType.REQ).connect("tcp://localhost:5559");
        for (int i = 0; i< 10; i++) {
            socket.send("Hello".getBytes());
            byte[] response = socket.receive();
            System.out.printf("Received reply %d [%s]%n", i, new String(response));
        }
        context.close();
    }
}
