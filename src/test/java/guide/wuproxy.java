package guide;

import org.zeromq.api.MessageFlag;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class wuproxy {

    public static void main(String[] args) throws Exception {
        ManagedContext context = new ManagedContext();

        //this is where the weather server sits
        Socket frontEnd = context.buildSocket(SocketType.SUB)
                .asSubscribable().subscribe("".getBytes())
                .connect("tcp://192.168.55.210:5556");

        //this is our public endpoint for subscribers
        Socket backend = context.buildSocket(SocketType.PUB).bind("tcp://10.1.1.0:8100");

        while (!Thread.currentThread().isInterrupted()) {
            boolean moreToReceive;
            do {
                byte[] message = frontEnd.receive();
                moreToReceive = frontEnd.hasMoreToReceive();
                backend.send(message, moreToReceive ? MessageFlag.SEND_MORE : MessageFlag.NONE);
            } while (moreToReceive);
        }

        context.close();
    }
}
