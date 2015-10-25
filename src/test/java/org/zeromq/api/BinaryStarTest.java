package org.zeromq.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;

import java.util.concurrent.atomic.AtomicInteger;

public class BinaryStarTest {
    private Context context;
    private Socket socket1;
    private Socket socket2;

    private Context context1;
    private BinaryStar primary;
    private Context context2;
    private BinaryStar backup;

    private AtomicInteger primaryVoter = new AtomicInteger();
    private AtomicInteger primaryActive = new AtomicInteger();
    private AtomicInteger primaryPassive = new AtomicInteger();
    private AtomicInteger backupVoter = new AtomicInteger();
    private AtomicInteger backupActive = new AtomicInteger();
    private AtomicInteger backupPassive = new AtomicInteger();

    @Before
    public void setUp() throws Exception {
        context = new ManagedContext();
        socket1 = context.buildSocket(SocketType.REQ)
            .connect("tcp://localhost:5557");
        socket2 = context.buildSocket(SocketType.REQ)
            .connect("tcp://localhost:5558");

        startPrimary();
        startBackup();
        Thread.sleep(100);
    }

    private void startPrimary() {
        context1 = new ManagedContext();
        primary = context1.buildBinaryStar()
            .withMode(BinaryStar.Mode.PRIMARY)
            .withLocalUrl("tcp://*:5555")
            .withRemoteUrl("tcp://localhost:5556")
            .withHeartbeatInterval(250)
            .withVoterSocket("tcp://*:5557")
            .withVoterHandler(new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Pollable pollable, Object... args) {
                    primaryVoter.incrementAndGet();
                    pollable.getSocket().send(pollable.getSocket().receiveMessage());
                    
                }
            })
            .withActiveHandler(new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Pollable pollable, Object... args) {
                    primaryActive.incrementAndGet();
                }
            })
            .withPassiveHandler(new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Pollable pollable, Object... args) {
                    primaryPassive.incrementAndGet();
                }
            })
            .start();
    }

    private void startBackup() {
        context2 = new ManagedContext();
        backup = context2.buildBinaryStar()
            .withMode(BinaryStar.Mode.BACKUP)
            .withLocalUrl("tcp://*:5556")
            .withRemoteUrl("tcp://localhost:5555")
            .withHeartbeatInterval(250)
            .withVoterSocket("tcp://*:5558")
            .withVoterHandler(new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Pollable pollable, Object... args) {
                    backupVoter.incrementAndGet();
                    pollable.getSocket().send(pollable.getSocket().receiveMessage());
                }
            })
            .withActiveHandler(new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Pollable pollable, Object... args) {
                    backupActive.incrementAndGet();
                }
            })
            .withPassiveHandler(new LoopHandler() {
                @Override
                public void execute(Reactor reactor, Pollable pollable, Object... args) {
                    backupPassive.incrementAndGet();
                }
            })
            .start();
    }

    @After
    public void tearDown() {
        primary.stop();
        context1.close();
        backup.stop();
        context2.close();

        context.close();
    }

    @Test
    public void testHeartBeat() throws Exception {
        Thread.sleep(550);

        assertEquals(1, primaryActive.get());
        assertEquals(0, primaryPassive.get());
        assertEquals(1, backupPassive.get());
        assertEquals(0, backupActive.get());
    }

    @Test
    public void testFailover() throws Exception {
        Thread.sleep(550);
        context1.terminate();
        context1.close();
        Thread.sleep(25);
        startPrimary();
        Thread.sleep(350);

        assertEquals(1, primaryActive.get());
        assertEquals(1, primaryPassive.get());
        assertEquals(1, backupPassive.get());
        assertEquals(1, backupActive.get());
    }

    @Test
    public void testVoter() throws Exception {
        Thread.sleep(550);
        ZInteger buf = new ZInteger();
        for (int i = 0; i < 10; i++) {
            buf.put(i).send(socket1);
            socket1.receive();
        }

        buf.put(1).send(socket2);
        Thread.sleep(25);
        assertNull(socket2.receive(MessageFlag.DONT_WAIT));

        Thread.sleep(25);
        assertEquals(10, primaryVoter.get());
        assertEquals(0, backupVoter.get());
        assertEquals(1, primaryActive.get());
        assertEquals(0, primaryPassive.get());
        assertEquals(1, backupPassive.get());
        assertEquals(0, backupActive.get());
    }
}
