package com.caitu99.gateway.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AppUtils {

    public static String getTopDomain(String url) throws URISyntaxException {
        String host = new URI(url).getHost().toLowerCase();
        Pattern pattern = Pattern.compile("[^\\.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)");
        Matcher matcher = pattern.matcher(host);
        String td = null;
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String MD5(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = password.getBytes();
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    public static boolean checkBefore(Date date) {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().before(date);
    }

    public static Object getInstance(String classname)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Object result = null;
        Class cls = null;
        cls = Class.forName(classname);
        if (cls != null) {
            result = cls.newInstance();// 被代理对象
        }
        return result;
    }

    public static String getHostAddressByConnection (String dstIp, int dspPort)
            throws IOException {
        Socket client = null;
        String address;
        try {
            client = new Socket(dstIp, dspPort);
            address = client.getLocalAddress().getHostAddress();
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return address;
    }

    public static String executeShellCommand(String command) {
        String status = "fail";
        Runtime run = Runtime.getRuntime();
        String result = "";
        BufferedReader br = null;
        BufferedInputStream in = null;

        try {
            Process p = run.exec(command);
            if(p.waitFor() != 0){
                status = getHostName();
                return status;
            }
            in = new BufferedInputStream(p.getInputStream());
            br = new BufferedReader(new InputStreamReader(in));
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                result += lineStr;
            }
        } catch (Exception e) {
            status = getHostName();
            return status;
        }finally{
            if(br!=null){
                try {
                    br.close();
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        return  result;
    }

    public static String getHostName() {
        String hostName = "unknownCarmen";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
        } catch (UnknownHostException ignored) {
        }
        return hostName;
    }
}
