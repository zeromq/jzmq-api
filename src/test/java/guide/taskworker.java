package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class taskworker {

    public static void main(String[] args) throws InterruptedException {
        ManagedContext context = new ManagedContext();
        Socket receiver = context.buildSocket(SocketType.PULL).connect("tcp://localhost:5557");
        Socket sink = context.buildSocket(SocketType.PUSH).connect("tcp://localhost:5558");

        while(true) {
            String workString = new String(receiver.receive());
            long workLoad = Long.parseLong(workString);
            System.out.print(workLoad + ".");

            //do the work
            Thread.sleep(workLoad);

            sink.send("".getBytes());
        }
    }
}
