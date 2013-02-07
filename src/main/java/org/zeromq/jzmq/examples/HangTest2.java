package org.zeromq.jzmq.examples;

import java.util.concurrent.CountDownLatch;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class HangTest2 {
    public static void main(String[] args) throws Exception {
        final Context ctx = ZMQ.context(1);
        final CountDownLatch signal = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket puller = ctx.socket(ZMQ.PULL);
                puller.setLinger(0);
                puller.bind("ipc:///tmp/hang.ipc");
                Socket pusher = ctx.socket(ZMQ.PUSH);
                pusher.setLinger(0);
                pusher.connect("ipc:///tmp/hang.ipc");
                System.out.println("WORKER: Sending PING");
                pusher.send("PING");
                System.out.println("WORKER: Received " + new String(puller.recv(0)));
                pusher.close();
                System.out.println("WORKER: Closed pusher");
                puller.close();
                System.out.println("WORKER: Closed puller");
                signal.countDown();
            }
        }).start();
        System.out.println("Waiting for sockets to close");
        signal.await();
        System.out.println("About to term context");
        ctx.term();
        System.out.println("termed context");
    }
}