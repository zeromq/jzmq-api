package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class hwclient {
    public static void main(String[] args) {
        ManagedContext context = new ManagedContext();
        Socket socket = context.buildSocket(SocketType.REQ).connect("tcp://localhost:5555");
        for (int i = 0; i< 10; i++) {
            socket.send("Hello".getBytes());

            byte[] response = socket.receive();
            System.out.println("response = " + new String(response));
        }
    }
}
