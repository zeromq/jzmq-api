package org.zeromq.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zeromq.ZMQException;
import org.zeromq.jzmq.ManagedContext;

public class RouterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ManagedContext context;

    @Before
    public void setUp() {
        context = new ManagedContext();
    }

    @After
    public void tearDown() throws Exception{
        context.close();
    }

    @Test
    public void testRouterMandatory() throws Exception {
        Socket testClass = context.buildSocket(SocketType.ROUTER)
                .asRoutable().withRouterMandatory()
                .bind("inproc://testRouter");

        Socket endpoint = context.buildSocket(SocketType.REQ).connect("inproc://testRouter");

        thrown.expect(ZMQException.class);
        thrown.expectMessage("No route to host");

        endpoint.send("test".getBytes());
        testClass.send("hello".getBytes(), MessageFlag.SEND_MORE);
        testClass.send(new byte[0], MessageFlag.SEND_MORE);
        testClass.send("message".getBytes());
    }

}
