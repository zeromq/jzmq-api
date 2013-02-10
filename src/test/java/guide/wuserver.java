package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

import java.util.Random;

public class wuserver {
    public static void main(String[] args) {
        ManagedContext context = new ManagedContext();

        Socket socket = context.buildSocket(SocketType.PUB).bind("tcp://*:5556", "ipc://weather");

        Random random = new Random(System.currentTimeMillis());
        while (true) {
            int zipCode = random.nextInt(100000) + 1;
            int temperature = random.nextInt(215) - 80 + 1;
            int relativeHumidity = random.nextInt(50) + 10 + 1;
            String update = String.format("%05d %d %d", zipCode, temperature, relativeHumidity);
            socket.send(update.getBytes());
        }
    }

}
