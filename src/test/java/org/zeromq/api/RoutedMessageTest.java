package org.zeromq.api;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zeromq.api.Message.Frame;

public class RoutedMessageTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetRoutes() throws Exception {
        RoutedMessage testClass = new RoutedMessage();
        testClass.addFrames(new Message(
                Arrays.asList(new Message.Frame("address1".getBytes()),
                        RoutedMessage.Route.BLANK,
                        new Message.Frame("payload".getBytes()))));

        List<RoutedMessage.Route> result = testClass.getRoutes();
        assertEquals(1, result.size());
        assertEquals(new RoutedMessage.Route("address1".getBytes()), result.get(0));
    }

    @Test
    public void testGetRoutes_multiple() throws Exception {
        RoutedMessage testClass = new RoutedMessage();
        testClass.addFrames(new Message(Arrays.asList(
                new Message.Frame("address1".getBytes()),
                RoutedMessage.Route.BLANK,
                new Message.Frame("address2".getBytes()),
                RoutedMessage.Route.BLANK,
                new Message.Frame("payload".getBytes()))));

        List<RoutedMessage.Route> result = testClass.getRoutes();
        assertEquals(2, result.size());
        assertEquals(new RoutedMessage.Route("address1".getBytes()), result.get(0));
        assertEquals(new RoutedMessage.Route("address2".getBytes()), result.get(1));
    }

    @Test
    public void testPayload_multipleRoutes() throws Exception {
        RoutedMessage testClass = new RoutedMessage();
        testClass.addFrames(new Message(Arrays.asList(
                new Message.Frame("address1".getBytes()),
                RoutedMessage.Route.BLANK,
                new Message.Frame("address2".getBytes()),
                RoutedMessage.Route.BLANK,
                new Message.Frame("payload".getBytes()))));

        Message result = testClass.getPayload();
        assertArrayEquals("payload".getBytes(), result.getFirstFrame().getData());
    }

    @Test
    public void testGetRoutes_noRoutes() throws Exception {
        RoutedMessage testClass = new RoutedMessage();
        testClass.addFrame(new Frame("payload"));

        List<RoutedMessage.Route> result = testClass.getRoutes();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testUnwrap_noRoutes() throws Exception {
        RoutedMessage testClass = new RoutedMessage();
        testClass.addFrame(new Frame("payload"));

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cannot unwrap an unrouted message.");

        testClass.unwrap();
    }

    @Test
    public void testUnwrap() throws Exception {
        RoutedMessage testClass = new RoutedMessage();
        testClass.addFrames(new Message(Arrays.asList(
                new Message.Frame("address1".getBytes()),
                RoutedMessage.Route.BLANK,
                new Message.Frame("address2".getBytes()),
                RoutedMessage.Route.BLANK,
                new Message.Frame("payload".getBytes()))));


        assertEquals(2, testClass.getRoutes().size());

        assertArrayEquals("address1".getBytes(), testClass.unwrap().getAddress());
        assertEquals(1, testClass.getRoutes().size());

        assertArrayEquals("address2".getBytes(), testClass.unwrap().getAddress());
        assertEquals(0, testClass.getRoutes().size());
    }


}
