package com.caitu99.gateway.utils.debug;

public class HotSwapClassLoader extends ClassLoader {

    public HotSwapClassLoader() {
        super(HotSwapClassLoader.class.getClassLoader());
    }

    public Class loadFromBytes(byte[] classBytes) {
        return this.defineClass(null, classBytes, 0, classBytes.length);
    }

}
