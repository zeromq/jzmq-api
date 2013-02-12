package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

public class msgqueue {
    public static void main(String[] args) throws IOException {
        ManagedContext context = new ManagedContext();

        Socket frontEnd = context.buildSocket(SocketType.ROUTER).bind("tcp://*:5559");
        Socket backEnd = context.buildSocket(SocketType.DEALER).bind("tcp://*:5560");

        context.proxy(frontEnd, backEnd);
        context.close();
    }
}
