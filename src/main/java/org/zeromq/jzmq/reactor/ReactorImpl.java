package org.zeromq.jzmq.reactor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.LoopHandler;
import org.zeromq.api.Pollable;
import org.zeromq.api.Poller;
import org.zeromq.api.Reactor;
import org.zeromq.api.exception.ContextTerminatedException;
import org.zeromq.api.exception.InvalidSocketException;
import org.zeromq.jzmq.ManagedContext;

public class ReactorImpl implements Reactor, Runnable {
    private static final Logger log = LoggerFactory.getLogger(Reactor.class);

    private final Thread thread = new Thread(this);
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final Poller poller;
    private final List<PollItem> pollItems;
    private final Queue<ReactorTimer> timers;

    public ReactorImpl(ManagedContext context) {
        this.pollItems = new ArrayList<>();
        this.timers = new PriorityQueue<>();
        this.poller = context.buildPoller().build();
    }

    @Override
    public void addPollable(Pollable pollable, LoopHandler handler, Object... args) {
        PollItem pollItem = new PollItem(this, pollable, handler, args);
        pollItems.add(pollItem);
        poller.register(pollable, pollItem);
    }

    @Override
    public void addTimer(long initialDelay, int numIterations, LoopHandler handler, Object... args) {
        ReactorTimer timer = new ReactorTimer(initialDelay, numIterations, handler, args);
        timer.recalculate(System.currentTimeMillis());

        timers.add(timer);
    }

    @Override
    public void cancel(LoopHandler handler) {
        // find the handler in pollers
        for (Iterator<PollItem> it = pollItems.iterator(); it.hasNext();) {
            PollItem item = it.next();
            if (item.handler == handler) {
                it.remove();
                if (poller != null) {
                    if (item.pollable.getChannel() != null) {
                        poller.disable(item.pollable.getChannel());
                    } else {
                        poller.disable(item.pollable.getSocket());
                    }
                }
            }
        }

        // find the handler in timers
        for (Iterator<ReactorTimer> it = timers.iterator(); it.hasNext();) {
            if (it.next().handler == handler) {
                it.remove();
            }
        }
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        running.set(false);
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void run() {
        running.set(true);

        // Main reactor loop
        while (running.get()) {
            long wait = ticklessTimer();
            try {
                /*
                 * Pollers will execute internally.
                 * 
                 * NOTE: This call can cause new timers and pollers to be
                 * registered internally, using a handle to this Reactor.
                 */
                poller.poll(wait);
            } catch (ContextTerminatedException | InvalidSocketException ex) {
                break;
            }

            // Break out early if shutting down
            if (!running.get()) {
                break;
            }

            // Handle any timers that have now expired
            long now = System.currentTimeMillis();
            while (!timers.isEmpty()
                    && timers.peek().nextFireTime <= now) {
                /*
                 * Remove timer from queue to execute it.
                 *
                 * NOTE: This call can cause new timers and pollers to be
                 * registered internally, using a handle to this Reactor.
                 */
                ReactorTimer timer = timers.poll();
                timer.execute(this);

                // Re-add repeating timer
                if (timer.numIterations > 0 || timer.numIterations == -1) {
                    timer.recalculate(now);
                    timers.add(timer);
                }
            }
        }

        log.info("Exiting reactor");
    }

    private long ticklessTimer() {
        // Calculate tickless timer, up to 1 hour
        long now = System.currentTimeMillis();
        long tickless = now + 1000 * 3600;
        if (timers.peek() != null) {
            tickless = timers.peek().nextFireTime;
        }

        long timeout = tickless - now;
        if (timeout < 0) {
            timeout = 0;
        }

        return timeout;
    }
}
