package org.zeromq.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.Patterns;
import org.zeromq.api.CloneClient;

import java.util.Random;

public class CloneClientMain {
    private static final Logger log = LoggerFactory.getLogger(CloneClientMain.class);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        final Thread thread = Thread.currentThread();
        final CloneClient cloneClient = Patterns.newCloneClient("localhost", "localhost", "/client/");

        Random rand = new Random(System.nanoTime());
        while (!thread.isInterrupted()) {
            //  Set random value, check it was stored
            String key = String.format("%s%d", "/client/", rand.nextInt(10000));
            String value = String.format("%d", rand.nextInt(1000000));
            log.info("Publishing update: {}={}", key, value);
            cloneClient.set(key, value, rand.nextInt(30));
            Thread.sleep(1000);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Stopping CloneClient...");
                thread.interrupt();
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
                log.info("CloneClient stopped");
            }
        });
    }
}
