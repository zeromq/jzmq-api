package org.zeromq.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.api.exception.ZMQRuntimeException;
import org.zeromq.jzmq.ManagedContext;

public class RouterTest {
    private ManagedContext context;

    @Before
    public void setUp() {
        context = new ManagedContext();
    }

    @After
    public void tearDown() throws Exception{
        context.close();
    }

    @Test(expected=ZMQRuntimeException.class)
    public void testRouterMandatory() throws Exception {
        Socket testClass = context.buildSocket(SocketType.ROUTER)
                .asRoutable().withRouterMandatory()
                .bind("inproc://testRouter");

        Socket endpoint = context.buildSocket(SocketType.REQ).connect("inproc://testRouter");

        endpoint.send("test".getBytes());
        testClass.send("hello".getBytes(), MessageFlag.SEND_MORE);
        testClass.send(new byte[0], MessageFlag.SEND_MORE);
        testClass.send("message".getBytes());
    }

}
