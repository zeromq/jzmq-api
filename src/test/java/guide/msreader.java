package guide;

import org.zeromq.api.MessageFlag;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class msreader {
    public static void main(String[] args) throws Exception {
        ManagedContext context = new ManagedContext();

        //connect to the task ventilator
        final Socket receiver = context.buildSocket(SocketType.PULL).connect("tcp://localhost:5557");

        //connect to the weather server
        final Socket subscriber = context.buildSocket(SocketType.SUB).asSubscribable()
                .subscribe("10001 ".getBytes())
                .connect("tcp://localhost:5556");

        while (true) {
            byte[] task;
            while ((task = receiver.receive(MessageFlag.DONT_WAIT)) != null) {
                System.out.println("task = " + new String(task));
            }

            byte[] update;
            while ((update = subscriber.receive(MessageFlag.DONT_WAIT)) != null) {
                System.out.println("update = " + new String(update));
            }

            Thread.sleep(1000L);
        }
    }
}
