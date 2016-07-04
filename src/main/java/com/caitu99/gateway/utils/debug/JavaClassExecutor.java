package com.caitu99.gateway.utils.debug;

import java.lang.reflect.Method;

public class JavaClassExecutor {

    public static String execute(byte[] bytes) {
        HackSystem.clearBuffer();
        ClassModifier cm = new ClassModifier(bytes);
        byte[] modifiedBytes = cm.modifyUTF8Constant("java/lang/System",
                "com/youzan/carmen/utils/debug/HackSystem");
        HotSwapClassLoader loader = new HotSwapClassLoader();
        Class clazz = loader.loadFromBytes(modifiedBytes);
        try {
            Method method = clazz.getMethod("main", new Class[]{String[].class});
            method.invoke(null, new String[]{null});
        } catch (Throwable throwable) {
            throwable.printStackTrace(HackSystem.out);
        }
        return HackSystem.getBufferString();
    }

}
