package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

public class mtrelay {

    public static void main(String[] args) throws IOException {
        final ManagedContext context = new ManagedContext();
        Socket receiver = context.buildSocket(SocketType.PAIR).bind("inproc://step3");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket receiver = context.buildSocket(SocketType.PAIR).bind("inproc://step2");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Socket sender = context.buildSocket(SocketType.PAIR).connect("inproc://step2");
                        sender.send("".getBytes());
                    }
                }).start();
                byte[] message = receiver.receive();
                Socket sender = context.buildSocket(SocketType.PAIR).connect("inproc://step3");
                sender.send(message);
            }
        }).start();
        receiver.receive();
        System.out.println("Test successful!");
        context.close();
    }
}
