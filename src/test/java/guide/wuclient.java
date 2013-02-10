package guide;

import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;

public class wuclient {

    public static void main(String[] args) {
        String filter = args.length == 0 ? "10001 " : args[0];

        ManagedContext context = new ManagedContext();
        Socket socket = context.buildSocket(SocketType.SUB).asSubscribable()
                .subscribe(filter.getBytes())
                .connect("ipc://weather");

        long totalTemperature = 0L;
        int temperatureReadings = 100;
        for (int i = 0; i < temperatureReadings; i++) {
            byte[] bytes = socket.receive();
            String weatherUpdate = new String(bytes);
            System.out.println("weatherUpdate = " + weatherUpdate);
            String[] pieces = weatherUpdate.split(" ");
            int zipCode = Integer.valueOf(pieces[0]);
            int temperature = Integer.valueOf(pieces[1]);
            int relativeHumidity = Integer.valueOf(pieces[2]);
            totalTemperature += temperature;
        }

        long averageTemperature = totalTemperature / temperatureReadings;
        System.out.println("average temperature = " + averageTemperature);
    }
}
