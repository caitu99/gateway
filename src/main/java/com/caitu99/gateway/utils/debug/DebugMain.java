package com.caitu99.gateway.utils.debug;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DebugMain {

    public static void main(String args[]) throws IOException {
        InputStream is = new FileInputStream("/Users/bobo/projects/pro/java/java/Test.class");
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        is.close();
        String result = JavaClassExecutor.execute(bytes);
    }

}
