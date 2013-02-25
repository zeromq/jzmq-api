package guide;

import org.zeromq.api.Message;
import org.zeromq.api.Socket;
import org.zeromq.jzmq.sockets.SocketBuilder;

import java.util.Random;

//todo should this be promoted to something like "DebugUtils" and moved into the main API code?
public class GuideHelper {
    private static Random rand = new Random(System.currentTimeMillis());

    /**
     * Receives all message parts from socket, prints neatly
     */
    public static void dump(Socket socket) {
        System.out.println("----------------------------------------");
        while (true) {
            byte[] msg = socket.receive();
            print(msg);
            if (!socket.hasMoreToReceive()) {
                break;
            }
        }
    }

    private static void print(byte[] msg) {
        boolean isText = true;
        String data = "";
        for (int i = 0; i < msg.length; i++) {
            if (msg[i] < 32 || msg[i] > 127) {
                isText = false;
            }
            data += String.format("%02X", msg[i]);
        }
        if (isText) {
            data = new String(msg);
        }

        System.out.println(String.format("[%03d] %s", msg.length, data));
    }

    public static void dump(Message message) {
        System.out.println("------------------------------------------");
        for (Message.Frame frame : message.getFrames()) {
            print(frame.getData());
        }
    }

    public static SocketBuilder assignPrintableIdentity(SocketBuilder builder) {
        String identity = String.format("%04X-%04X", rand.nextInt(), rand.nextInt());

        builder.withIdentity(identity.getBytes());
        return builder;
    }

}
