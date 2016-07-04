package com.caitu99.gateway.utils.debug;

public class ByteUtils {

    public static int bytes2Int(byte[] bytes, int start, int len) {
        int sum = 0;
        int end = start + len;
        for (int i = start; i < end; i++) {
            int n = (int) bytes[i] & 0xff;
            n <<= (--len) * 8;
            sum = n + sum;
        }
        return sum;
    }

    public static byte[] int2Bytes(int value, int len) {
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[len - i - 1] = (byte) (value >> 8 * i & 0xff);
        }
        return bytes;
    }

    public static String bytes2String(byte[] bytes, int start, int len) {
        return new String(bytes, start, len);
    }

    public static byte[] string2Bytes(String string) {
        return string.getBytes();
    }

    public static byte[] bytesReplace(byte[] originBytes, int offset, int len, byte[] replaceBytes) {
        byte[] newBytes = new byte[originBytes.length + replaceBytes.length - len];
        System.arraycopy(originBytes, 0, newBytes, 0, offset);
        System.arraycopy(replaceBytes, 0, newBytes, offset, replaceBytes.length);
        System.arraycopy(originBytes, offset + len, newBytes, offset + replaceBytes.length,
                originBytes.length - offset - len);
        return newBytes;
    }

}
