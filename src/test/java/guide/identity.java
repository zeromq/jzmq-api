package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.io.IOException;

import static guide.GuideHelper.dump;

public class identity {
    public static void main(String[] args) throws IOException {
        ManagedContext context = new ManagedContext();

        Socket sink = context.buildSocket(SocketType.ROUTER).bind("inproc://example");

        Socket anonymous = context.buildSocket(SocketType.REQ).connect("inproc://example");
        anonymous.send("ROUTER uses a generated UUID".getBytes());
        dump(sink);

        Socket identified = context.buildSocket(SocketType.REQ).withIdentity("PEER2".getBytes())
                .connect("inproc://example");

        identified.send("ROUTER socket uses REQ's socket identity".getBytes());
        dump(sink);

        context.close();
    }
}
