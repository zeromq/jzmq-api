package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class psenvsub {
    public static void main(String[] args) {
        ManagedContext context = new ManagedContext();
        Socket subscriber = context.buildSocket(SocketType.SUB)
                .asSubscribable().subscribe("B".getBytes())
                .connect("tcp://localhost:5563");

        while (true) {
            String address = new String(subscriber.receive());
            String contents = new String(subscriber.receive());
            System.out.printf("%s : %s%n", address, contents);
        }
    }
}
