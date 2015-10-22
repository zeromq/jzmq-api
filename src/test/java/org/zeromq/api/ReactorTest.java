package org.zeromq.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.reactor.ReactorBuilder;

import java.util.concurrent.atomic.AtomicInteger;

public class ReactorTest {
    private ManagedContext context;
    private ManagedContext shadow;
    private Socket in;
    private Socket out;

    private AtomicInteger safe = new AtomicInteger();

    @Before
    public void setUp() throws Exception {
        context = new ManagedContext();
        shadow = context.shadow();
        out = shadow.buildSocket(SocketType.PUB).bind("inproc://test");
        in = shadow.buildSocket(SocketType.SUB).asSubscribable().subscribe("".getBytes()).connect("inproc://test");
        Thread.sleep(15);
    }

    @After
    public void tearDown() throws Exception {
        new Thread() {
            @Override
            public void run() {
                // terminate context without closing sockets to shut down reactor thread
                context.close();
            }
        }.start();

        Thread.sleep(15);
        shadow.close();
    }

    @Test
    public void testTimer() throws Exception {
        Reactor reactor = context.buildReactor()
            .withTimer(10, 25, new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    safe.incrementAndGet();
                }
            })
            .build();

        reactor.start();
        Thread.sleep(500);
        assertEquals(25, safe.get());
    }

    @Test
    public void testTimers_1000() throws Exception {
        LoopHandler handler = new LoopHandler() {
            @Override
            public void execute(Reactor reactor, Socket socket, Object... args) {
                safe.incrementAndGet();
            }
        };

        ReactorBuilder builder = context.buildReactor();
        for (int i = 0; i < 1000; i++) {
            builder.withTimer(100, (i % 10) + 1, handler);
        }

        Reactor reactor = builder.build();
        reactor.start();
        Thread.sleep(1250);
        assertEquals(5500, safe.get());
    }
    
    @Test
    public void testTimers_100000() throws Exception {
        LoopHandler handler = new LoopHandler() {
            @Override
            public void execute(Reactor reactor, Socket socket, Object... args) {
                safe.incrementAndGet();
            }
        };

        ReactorBuilder builder = context.buildReactor();
        for (int i = 0; i < 100000; i++) {
            builder.withTimer(100, (i % 10) + 1, handler);
        }

        Reactor reactor = builder.build();
        reactor.start();
        Thread.sleep(1250);
        assertEquals(550000, safe.get());
    }

    @Test
    public void testPoller() throws Exception {
        Reactor reactor = context.buildReactor()
            .withInPollable(in, new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    assertEquals("Hello", new String(socket.receive()));
                    safe.incrementAndGet();
                }
            })
            .build();

        reactor.start();
        out.send("Hello".getBytes());
        out.send("Hello".getBytes());
        Thread.sleep(50);
        out.send("Hello".getBytes());
        Thread.sleep(250);
        assertEquals(3, safe.get());
    }

    @Test
    public void testPollers_1000() throws Exception {
        ReactorBuilder builder = context.buildReactor();
        LoopHandler handler = new LoopHandler() {
            @Override
            public void execute(Reactor reactor, Socket socket, Object... args) {
                assertEquals("Hello", new String(socket.receive()));
                safe.incrementAndGet();
            }
        };

        for (int i = 0; i < 1000; i++) {
            Socket s = shadow.buildSocket(SocketType.SUB)
                    .asSubscribable().subscribe("".getBytes())
                    .connect("inproc://test");
            builder.withInPollable(s, handler);
        }

        Reactor reactor = builder.build();
        reactor.start();
        out.send("Hello".getBytes());
        out.send("Hello".getBytes());
        Thread.sleep(50);
        out.send("Hello".getBytes());
        Thread.sleep(250);
        assertEquals(3000, safe.get());
    }

    @Test
    public void testMultiple() throws Exception {
        Reactor reactor = context.buildReactor()
            .withTimer(10, 25, new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    safe.incrementAndGet();
                }
            })
            .withInPollable(in, new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    assertEquals("Hello", new String(socket.receive()));
                    safe.incrementAndGet();
                }
            })
            .build();

        reactor.start();
        out.send("Hello".getBytes());
        out.send("Hello".getBytes());
        out.send("Hello".getBytes());
        Thread.sleep(500);
        assertEquals(28, safe.get());
    }
}
