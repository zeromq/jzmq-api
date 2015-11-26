package org.zeromq.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;

import java.util.concurrent.atomic.AtomicInteger;

public class BinaryStarReactorTest {
    private Context context;
    private Socket socket;

    private Context context1;
    private BinaryStarReactor primary;
    private Context context2;
    private BinaryStarReactor backup;

    private AtomicInteger primaryVoter = new AtomicInteger();
    private AtomicInteger primaryActive = new AtomicInteger();
    private AtomicInteger primaryPassive = new AtomicInteger();
    private AtomicInteger backupVoter = new AtomicInteger();
    private AtomicInteger backupActive = new AtomicInteger();
    private AtomicInteger backupPassive = new AtomicInteger();

    @Before
    public void setUp() throws Exception {
        context = new ManagedContext();
        socket = context.buildBinaryStarSocket()
            .withHeartbeatInterval(250)
            .connect("tcp://localhost:5557", "tcp://localhost:5558");

        startPrimary();
        startBackup();
        Thread.sleep(100);
    }

    @After
    public void tearDown() {
        if (context1 != null) {
            primary.stop();
            context1.close();
        }
        if (context2 != null) {
            backup.stop();
            context2.close();
        }

        context.terminate();
        context.close();
    }

    private void startPrimary() {
        context1 = new ManagedContext();
        primary = context1.buildBinaryStarReactor()
            .withMode(BinaryStarReactor.Mode.PRIMARY)
            .withLocalUrl("tcp://*:5555")
            .withRemoteUrl("tcp://localhost:5556")
            .withHeartbeatInterval(250)
            .withVoterSocket("tcp://*:5557")
            .withVoterHandler(new LoopAdapter() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    primaryVoter.incrementAndGet();
                    socket.send(socket.receiveMessage());
                    
                }
            })
            .withActiveHandler(new LoopAdapter() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    primaryActive.incrementAndGet();
                }
            })
            .withPassiveHandler(new LoopAdapter() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    primaryPassive.incrementAndGet();
                }
            })
            .start();
    }

    private void startBackup() {
        context2 = new ManagedContext();
        backup = context2.buildBinaryStarReactor()
            .withMode(BinaryStarReactor.Mode.BACKUP)
            .withLocalUrl("tcp://*:5556")
            .withRemoteUrl("tcp://localhost:5555")
            .withHeartbeatInterval(250)
            .withVoterSocket("tcp://*:5558")
            .withVoterHandler(new LoopAdapter() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    backupVoter.incrementAndGet();
                    socket.send(socket.receiveMessage());
                }
            })
            .withActiveHandler(new LoopAdapter() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    backupActive.incrementAndGet();
                }
            })
            .withPassiveHandler(new LoopAdapter() {
                @Override
                public void execute(Reactor reactor, Socket socket, Object... args) {
                    backupPassive.incrementAndGet();
                }
            })
            .start();
    }

    private void stopPrimary() {
        context1.terminate();
        context1.close();
        primary = null;
        context1 = null;
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
        stopPrimary();

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

        for (int i = 0; i < 10; i++) {
            socket.send(new Message(i));
            socket.receive();
        }

        assertEquals(10, primaryVoter.get());
        assertEquals(0, backupVoter.get());
        assertEquals(1, primaryActive.get());
        assertEquals(0, primaryPassive.get());
        assertEquals(1, backupPassive.get());
        assertEquals(0, backupActive.get());
    }

    @Test
    public void testVoterFailover() throws Exception {
        Thread.sleep(550);

        socket.send(new Message(1));
        socket.receiveMessage();

        stopPrimary();

        socket.send(new Message(1));
        socket.receiveMessage();

        assertEquals(1, primaryVoter.get());
        assertEquals(1, backupVoter.get());
        assertEquals(1, primaryActive.get());
        assertEquals(0, primaryPassive.get());
        assertEquals(1, backupPassive.get());
        assertEquals(1, backupActive.get());
    }
}
