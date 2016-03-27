package org.zeromq.api;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ContextFactory;

public class ForkTest {
    private Context context;

    @Before
    public void setUp() {
        context = ContextFactory.createContext(1);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFork() throws Exception {
        Socket pipe = context.fork(new Backgroundable() {
            @Override
            public void run(Context context, Socket pipe, Object... args) {
                pipe.send("hello".getBytes());
                assertEquals("hi", new String(pipe.receive()));
            }

            @Override
            public void onClose() {
                // TODO Auto-generated method stub

            }
        });

        assertEquals("hello", new String(pipe.receive()));
        pipe.send("hi".getBytes());

        context.close();
    }

    @Test
    public void testBackgroundable() throws Exception {
        final AtomicBoolean closed = new AtomicBoolean(false);
        Socket req = context.buildSocket(SocketType.REQ)
            .bind("inproc://background-test");

        // background thread will handle this socket
        context.buildSocket(SocketType.REP)
            .withBackgroundable(new Backgroundable() {
                @Override
                public void run(Context context, Socket socket, Object... args) {
                    assertEquals("hello", new String(socket.receive()));
                    socket.send("hello, world".getBytes());
                }

                @Override
                public void onClose() {
                    closed.set(true);
                }
            })
            .connect("inproc://background-test");

        req.send("hello".getBytes());
        assertEquals("hello, world", new String(req.receive()));

        context.close();
        assertTrue(closed.get());
    }

}
