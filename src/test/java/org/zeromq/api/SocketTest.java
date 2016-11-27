package org.zeromq.api;

import static junit.framework.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.api.exception.ContextTerminatedException;
import org.zeromq.api.exception.InvalidSocketException;
import org.zeromq.jzmq.ManagedContext;

public class SocketTest {

    private static final Logger log = LoggerFactory.getLogger(SocketTest.class);
    private ManagedContext context;
    
    @Before
    public void setUp() {
        this.context = new ManagedContext(1);
    }
    
    @After
    public void tearDown() {
        context.close();
    }
    
    @Test(expected=InvalidSocketException.class)
    public void testClosedSocket() {
        Context shadow = context.shadow();
        Socket pub = shadow.buildSocket(SocketType.PUB)
                .bind("inproc://socket-test");
        Socket sub = shadow.buildSocket(SocketType.SUB)
                .asSubscribable()
                .subscribe("".getBytes())
                .connect("inproc://socket-test");
        
        pub.send("hello".getBytes());
        assertEquals("hello", new String(sub.receive()));
        
        shadow.close();
        pub.send("hello, world".getBytes());
    }
    
    @Test(timeout=1000)
    public void testTerminatedContext() throws InterruptedException {
        Socket req = context.buildSocket(SocketType.REQ)
                .bind("inproc://socket-test");
        
        ManagedContext shadow = context.shadow();
        /*final Socket rep = */
        shadow.buildSocket(SocketType.REP)
                .withBackgroundable(new Backgroundable() {
                    @Override
                    public void run(Context context, Socket socket) {
                        assertEquals("hello", new String(socket.receive()));
                        socket.send("hello, world".getBytes());
                        try {
                            socket.receive();
                        } catch (ContextTerminatedException ignored) {
                        }
                        
                        socket.close();
                        log.info("Closed REP socket, exiting...");
                    }
                    
                    @Override
                    public void onClose() {
                        // TODO Auto-generated method stub
                        
                    }
                })
                .connect("inproc://socket-test");
        
        req.send("hello".getBytes());
        assertEquals("hello, world", new String(req.receive()));
        
        Thread.sleep(10);
        context.close();
    }

}
