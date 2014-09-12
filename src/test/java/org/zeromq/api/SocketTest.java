package org.zeromq.api;

import static junit.framework.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ContextFactory;
import org.zeromq.api.exception.InvalidSocketException;

public class SocketTest {

    private Context context;
    
    @Before
    public void setUp() {
        this.context = ContextFactory.createContext(1);
    }
    
    @After
    public void tearDown() {
        context.close();
    }
    
    @Test(expected=InvalidSocketException.class)
    public void testClosedSocket() {
        Socket pub = context.buildSocket(SocketType.PUB)
                .bind("inproc://socket-test");
        Socket sub = context.buildSocket(SocketType.SUB)
                .asSubscribable()
                .subscribe("".getBytes())
                .connect("inproc://socket-test");
        
        pub.send("hello".getBytes());
        assertEquals("hello", new String(sub.receive()));
        
        context.close();
        pub.send("hello, world".getBytes());
    }

}
