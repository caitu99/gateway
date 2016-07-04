package com.caitu99.gateway.utils.debug;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

public class HackSystem {

    public final static InputStream in = System.in;
    private static ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    public final static PrintStream out = new PrintStream(buffer);
    public final static PrintStream err = out;

    public static String getBufferString() {
        return buffer.toString();
    }

    public static void clearBuffer() {
        buffer.reset();
    }

    public static void setSecurityManager(final SecurityManager securityManager) {
        System.setSecurityManager(securityManager);
    }

    public static SecurityManager getSecurityManager() {
        return System.getSecurityManager();
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    public static int identityHashCode(Object object) {
        return System.identityHashCode(object);
    }

    public static String clearProperty(String key) {
        return System.clearProperty(key);
    }

    public static Console console() {
        return System.console();
    }

    public static Properties getProperties() {
        return System.getProperties();
    }

    public static String lineSeparator() {
        return System.lineSeparator();
    }

    public static void setProperties(Properties props) {
        System.setProperties(props);
    }

    public static String getProperty(String key) {
        return System.getProperty(key);
    }

    public static String getProperty(String key, String def) {
        return System.getProperty(key, def);
    }

    public static String setProperty(String key, String value) {
        return System.setProperty(key, value);
    }

    public static String getenv(String name) {
        return System.getenv(name);
    }

    public static java.util.Map<String,String> getenv() {
        return System.getenv();
    }

    public static void exit(int status) {
        System.exit(status);
    }

    public static void gc() {
        System.gc();
    }

    public static void runFinalization() {
        System.runFinalization();
    }

    @Deprecated
    public static void runFinalizersOnExit(boolean value) {
        System.runFinalizersOnExit(value);
    }

    public static void load(String filename) {
        System.load(filename);
    }

    public static void loadLibrary(String libname) {
        System.loadLibrary(libname);
    }

    public static String mapLibraryName(String libname) {
        return System.mapLibraryName(libname);
    }

}
