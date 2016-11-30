package org.zeromq.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;

public class CloneServerTest {
    private Context context;
    private CloneServer primary;
    private CloneServer backup;
    private CloneClient client1;
    private CloneClient client2;

    @Before
    public void setUp() throws Exception {
        context = new ManagedContext();

        primary = context.buildCloneServer()
            .withHeartbeatInterval(200)
            .withMode(BinaryStarReactor.Mode.PRIMARY)
            .withPrimaryAddress("localhost")
            .withBackupAddress("localhost")
            .withPrimaryPort(5556)
            .withBackupPort(5566)
            .withPrimaryBinaryStarPort(5003)
            .withBackupBinaryStarPort(5004)
            .build();

        backup = context.buildCloneServer()
            .withHeartbeatInterval(200)
            .withMode(BinaryStarReactor.Mode.BACKUP)
            .withPrimaryAddress("localhost")
            .withBackupAddress("localhost")
            .withPrimaryPort(5556)
            .withBackupPort(5566)
            .withPrimaryBinaryStarPort(5003)
            .withBackupBinaryStarPort(5004)
            .build();

        primary.start();
        Thread.sleep(50);
        backup.start();
        Thread.sleep(1000);

        client1 = context.buildCloneClient()
            .withHeartbeatInterval(200)
            .withPrimaryAddress("localhost")
            .withBackupAddress("localhost")
            .withPrimaryPort(5556)
            .withBackupPort(5566)
            .withSubtree("/client/")
            .build();

        client2 = context.buildCloneClient()
            .withHeartbeatInterval(200)
            .withPrimaryAddress("localhost")
            .withBackupAddress("localhost")
            .withPrimaryPort(5556)
            .withBackupPort(5566)
            .withSubtree("/client/")
            .build();

        Thread.sleep(50);
    }

    @After
    public void tearDown() throws Exception {
        backup.stop();
        primary.stop();
        context.terminate();
        context.close();
    }

    @Test
    public void testSet() throws Exception {
        client1.set("/client/key", "value1", 60);
        Thread.sleep(50);
        assertEquals("value1", client1.get("/client/key"));
        assertEquals("value1", client2.get("/client/key"));

        client2.set("/client/key", "value2", 60);
        Thread.sleep(50);
        assertEquals("value2", client2.get("/client/key"));
        assertEquals("value2", client1.get("/client/key"));

        System.out.println("Finished");
    }
}
