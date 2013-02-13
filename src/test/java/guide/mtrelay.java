package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

public class mtrelay {

    public static void main(String[] args) throws IOException {
        final ManagedContext context = new ManagedContext();
        Socket receiver = context.buildSocket(SocketType.PAIR).bind("inproc://step3");

        Runnable step2 = new Runnable() {
            @Override
            public void run() {
                Socket receiver = context.buildSocket(SocketType.PAIR).bind("inproc://step2");

                Runnable step1 = new Runnable() {
                    @Override
                    public void run() {
                        Socket sender = context.buildSocket(SocketType.PAIR).connect("inproc://step2");
                        sender.send("".getBytes());
                    }
                };

                new Thread(step1).start();;

                byte[] message = receiver.receive();

                Socket sender = context.buildSocket(SocketType.PAIR).connect("inproc://step3");
                sender.send(message);
            }
        };

        new Thread(step2).start();

        receiver.receive();
        System.out.println("Test successful!");
        context.close();
    }
}
