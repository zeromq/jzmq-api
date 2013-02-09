package org.zeromq.jzmq;

import org.junit.Test;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;

public class ReqRepTest {
    @Test(timeout = 1000)
    public void testReqRep() throws Exception {
        ManagedContext context = new ManagedContext();
        final Socket repSocket = context.buildSocket(SocketType.REP).bind("inproc://serverSocket");
        Runnable server = new Runnable() {
            @Override
            public void run() {
                byte[] request = repSocket.receive();
                assertArrayEquals("request".getBytes(), request);
                repSocket.send("response".getBytes());
            }
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(server);
        Socket requestSocket = context.buildSocket(SocketType.REQ).connect("inproc://serverSocket");
        requestSocket.send("request".getBytes());
        byte[] response = requestSocket.receive();
        assertArrayEquals("response".getBytes(), response);

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        context.close();
    }
}
