package org.zeromq.api;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.beacon.BeaconReactorImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BeaconReactorTest {
    private ManagedContext context;
    private BeaconReactor reactor1;
    private BeaconReactor reactor2;
    private BeaconReactor reactor3;

    private AtomicInteger beacon1 = new AtomicInteger();
    private AtomicInteger beacon2 = new AtomicInteger();
    private AtomicInteger beacon3 = new AtomicInteger();

    @Before
    public void setUp() throws Exception {
        context = new ManagedContext();
        startBeaconReactor1();
        startBeaconReactor2();
    }

    @After
    public void tearDown() throws Exception {
        reactor1.stop();
        reactor2.stop();
        if (reactor3 != null) {
            reactor3.stop();
        }
        context.close();
    }

    private void startBeaconReactor2() throws IOException {
        reactor1 = context.buildBeaconReactor()
            .withPort(1234)
            .withIgnoreLocalAddress(false)
            .withBeacon(new UdpBeacon(1, "TEST", UUID.randomUUID(), 1234))
            .withListener(new BeaconListener() {
                @Override
                public void onBeacon(InetAddress sender, UdpBeacon beacon) {
                    beacon2.incrementAndGet();
                }
            })
            .start();
    }

    private void startBeaconReactor1() throws IOException {
        reactor2 = context.buildBeaconReactor()
            .withPort(1234)
            .withIgnoreLocalAddress(false)
            .withBeacon(new UdpBeacon(1, "TEST", UUID.randomUUID(), 1234))
            .withListener(new BeaconListener() {
                @Override
                public void onBeacon(InetAddress sender, UdpBeacon beacon) {
                    beacon1.incrementAndGet();
                }
            })
            .start();
    }

    private void startBeaconReactor3() throws IOException {
        reactor3 = context.buildBeaconReactor()
            .withPort(1234)
            .withIgnoreLocalAddress(true)
            .withBeacon(new UdpBeacon(2, "X", UUID.randomUUID(), 1234))
            .withListener(new BeaconListener() {
                @Override
                public void onBeacon(InetAddress sender, UdpBeacon beacon) {
                    beacon3.incrementAndGet();
                }
            })
            .start();
    }

    @Test
    public void testBeaconReactor() throws Exception {
        Thread.sleep(2250);

        assertEquals(4, beacon1.get());
        assertEquals(4, beacon2.get());
    }

    @Test
    public void testBeaconReactor_InvalidBeacon() throws Exception {
        startBeaconReactor3();
        Thread.sleep(2250);

        assertEquals(4, beacon1.get());
        assertEquals(4, beacon2.get());
        assertEquals(0, beacon3.get());
    }
}
