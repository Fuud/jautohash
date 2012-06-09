package com.blogspot.fuud.java.jautohash.agent.util;

public class Singleton {
    private static Object clazz;

    public static Object getClazz() {
        return clazz;
    }

    public static void setClazz(Object clazz) {
        Singleton.clazz = clazz;
    }
}
