package org.zeromq.api;

import static junit.framework.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ContextFactory;

public class ForkTest {

    private Context context;
    private boolean shutDown = false;
    
    @Before
    public void setUp() {
        this.context = ContextFactory.createContext(1);
    }
    
    @After
    public void tearDown() {
        context.close();
    }
    
    @Test
    public void testFork() throws Exception {
        Socket pipe = context.fork(new Backgroundable() {
            @Override
            public void run(Context context, Socket pipe, Object... args) {
                while (!shutDown) {
                    pipe.send("hello".getBytes());
                    assertEquals("hi", new String(pipe.receive()));
                }
            }
            
            @Override
            public void onClose() {
                // TODO Auto-generated method stub
                
            }
        });
        
        assertEquals("hello", new String(pipe.receive()));
        pipe.send("hi".getBytes());
        assertEquals("hello", new String(pipe.receive()));
        
        shutDown = true;
        pipe.send("hi".getBytes());
        
        context.close();
    }

}
