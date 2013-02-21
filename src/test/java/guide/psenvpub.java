package guide;

import org.zeromq.api.MessageFlag;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class psenvpub {
    public static void main(String[] args) throws InterruptedException {
        ManagedContext context = new ManagedContext();
        Socket publisher = context.buildSocket(SocketType.PUB).bind("tcp://*:5563");

        while (true) {
            publisher.send("A".getBytes(), MessageFlag.SEND_MORE);
            publisher.send("We don't want to see this".getBytes());

            publisher.send("B".getBytes(), MessageFlag.SEND_MORE);
            publisher.send("We would like to see this".getBytes());
            Thread.sleep(1000L);
        }
    }
}
