package org.zeromq.api;

class Bits {
    public static int getInt(byte[] buf) {
        return ((buf[0] & 0xff) << 24)
            + ((buf[1] & 0xff) << 16)
            + ((buf[2] & 0xff) << 8)
            + ((buf[3] & 0xff));
    }

    public static void putInt(byte[] buf, int val) {
        buf[0] = (byte) (val >> 24);
        buf[1] = (byte) (val >> 16);
        buf[2] = (byte) (val >> 8);
        buf[3] = (byte) (val);
    }

    public static long getLong(byte[] buf) {
        return ((long) (buf[0] & 0xff) << 56)
            + ((long) (buf[1] & 0xff) << 48)
            + ((long) (buf[2] & 0xff) << 40)
            + ((long) (buf[3] & 0xff) << 32)
            + ((buf[4] & 0xff) << 24)
            + ((buf[5] & 0xff) << 16)
            + ((buf[6] & 0xff) << 8)
            + ((buf[7] & 0xff));
    }

    public static void putLong(byte[] buf, long val) {
        buf[0] = (byte) (val >> 56);
        buf[1] = (byte) (val >> 48);
        buf[2] = (byte) (val >> 40);
        buf[3] = (byte) (val >> 32);
        buf[4] = (byte) (val >> 24);
        buf[5] = (byte) (val >> 16);
        buf[6] = (byte) (val >> 8);
        buf[7] = (byte) (val);
    }
}