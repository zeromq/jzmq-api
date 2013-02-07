package org.zeromq.jzmq.examples;

import java.util.concurrent.CountDownLatch;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class HangTest3 {
    public static void main(String[] args) throws Exception {
        final Context ctx = ZMQ.context(1);
        Socket puller = ctx.socket(ZMQ.PULL);
        puller.setLinger(0);
        puller.bind("ipc:///tmp/hang.ipc");
        Socket pusher = ctx.socket(ZMQ.PUSH);
        pusher.setLinger(0);
        pusher.connect("ipc:///tmp/hang.ipc");
        System.out.println("Sending ping");
        pusher.send("PING");
        System.out.println("Received " + new String(puller.recv(0)));
        puller.close();
        pusher.close();
        final CountDownLatch signal = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("WORKER: About to call term");
                ctx.term();
                System.out.println("WORKER: Termed context");
                signal.countDown();
            }
        }).start();
        signal.await();
        System.out.println("All done");
    }
}