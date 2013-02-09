package org.zeromq.jzmq;

import org.junit.Test;
import org.zeromq.api.Context;
import org.zeromq.api.Socket;
import org.zeromq.api.SocketType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PubSubTest {

    @Test(timeout = 1000)
    public void testPubSub() throws Exception {
        Context context = new ManagedContext();
        Socket publisher = context.createSocket(SocketType.PUB).bind("inproc://publisher");

        final AtomicBoolean resultSeen = new AtomicBoolean(false);
        final Socket subscriber = context.createSubSocket().subscribe("".getBytes()).connect("inproc://publisher");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                byte[] contents = subscriber.receive();
                assertEquals("Hello", new String(contents));
                resultSeen.set(true);
            }
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(runnable);

        publisher.send("Hello".getBytes());

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        assertTrue(resultSeen.get());
        context.close();
    }
}
