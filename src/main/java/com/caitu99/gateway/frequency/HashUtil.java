package com.caitu99.gateway.frequency;


import java.security.MessageDigest;

/**
 * Created by chenyun on 15/7/30.
 */
public class HashUtil {

    public final static String hash(String inputStr) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                            'a', 'b', 'c', 'd', 'e', 'f'};
        String retRes = null;
        try {
            byte[] strBytes = inputStr.getBytes("UTF-8");
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(strBytes);
            byte[] md = digest.digest();
            int length = md.length;
            char str[] = new char[length * 2];
            int k = 0;
            for (int i = 0; i < length; i++) {
                byte b = md[i];
                str[k++] = hexDigits[b >>> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            retRes = new String(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return retRes;
    }

    public static void main(String[] args) {
    }

}
