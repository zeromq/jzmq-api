package org.zeromq.client;

import org.zeromq.api.LoopHandler;
import org.zeromq.api.Pollable;
import org.zeromq.api.Reactor;
import org.zeromq.api.SocketType;
import org.zeromq.jzmq.ManagedContext;
import org.zeromq.jzmq.bstar.BinaryStarReactorImpl;

import java.util.Arrays;

public class PrimaryStar {
    /**
     * @param args
     */
    public static void main(String[] args) {
        String local = "tcp://localhost:5555";
        if (args.length >= 1) {
            local = args[0];
        }

        String remote = "tcp://localhost:5556";
        if (args.length >= 2) {
            remote = args[1];
        }

        String voter = "tcp://localhost:5557";
        if (args.length >= 3) {
            remote = args[2];
        }

        ManagedContext context = new ManagedContext();
        BinaryStarReactorImpl binaryStar = new BinaryStarReactorImpl(context, BinaryStarReactorImpl.Mode.PRIMARY, local, remote);
        binaryStar.registerVoterSocket(context.buildSocket(SocketType.PULL).bind(voter));
        binaryStar.setVoterHandler(new MyHandler(), "primary-voter");
        binaryStar.setActiveHandler(new MyHandler(), "primary-active");
        binaryStar.setPassiveHandler(new MyHandler(), "primary-passive");
        binaryStar.start();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {
        }

        context.terminate();
        context.close();
    }

    public static class MyHandler implements LoopHandler {
        @Override
        public void execute(Reactor reactor, Pollable pollable, Object... args) {
            System.out.println(Arrays.asList(args).toString());
        }
    }
}
