package org.zeromq.api;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class BitsTest {
    @Test
    public void testPutInt_100() {
        byte[] buf = new byte[4];
        Bits.putInt(buf, 100);
        assertEquals(0, buf[0]);
        assertEquals(0, buf[1]);
        assertEquals(0, buf[2]);
        assertEquals(100, buf[3]);
    }

    @Test
    public void testGetInt_100() {
        byte[] buf = {0, 0, 0, 100};

        int y = Bits.getInt(buf);
        assertEquals(100, y);
    }

    @Test
    public void testPutInt_0x77777777() {
        byte[] buf = new byte[4];
        Bits.putInt(buf, 0x77777777);
        assertEquals(0x77, buf[0]);
        assertEquals(0x77, buf[1]);
        assertEquals(0x77, buf[2]);
        assertEquals(0x77, buf[3]);
    }

    @Test
    public void testGetInt_0x77777777() {
        byte[] buf = {0x77, 0x77, 0x77, 0x77};

        int y = Bits.getInt(buf);
        assertEquals(0x77777777, y);
    }

    @Test
    public void testPutLong_100() {
        byte[] buf = new byte[8];
        Bits.putLong(buf, 100);
        assertEquals(0, buf[0]);
        assertEquals(0, buf[1]);
        assertEquals(0, buf[2]);
        assertEquals(0, buf[3]);
        assertEquals(0, buf[4]);
        assertEquals(0, buf[5]);
        assertEquals(0, buf[6]);
        assertEquals(100L, buf[7]);
    }

    @Test
    public void testGetLong_100() {
        byte[] buf = {0, 0, 0, 0, 0, 0, 0, 100};

        long y = Bits.getLong(buf);
        assertEquals(100, y);
    }

    @Test
    public void testPutLong_0x7777777777777777() {
        byte[] buf = new byte[8];
        Bits.putLong(buf, 0x7777777777777777L);
        assertEquals(0x77, buf[0]);
        assertEquals(0x77, buf[1]);
        assertEquals(0x77, buf[2]);
        assertEquals(0x77, buf[3]);
        assertEquals(0x77, buf[4]);
        assertEquals(0x77, buf[5]);
        assertEquals(0x77, buf[6]);
        assertEquals(0x77, buf[7]);
    }

    @Test
    public void testGetLong_0x7777777777777777() {
        byte[] buf = {0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77, 0x77};

        long y = Bits.getLong(buf);
        assertEquals(0x7777777777777777L, y);
    }
}
