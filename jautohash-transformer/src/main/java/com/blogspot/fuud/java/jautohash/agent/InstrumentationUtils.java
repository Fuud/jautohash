package com.blogspot.fuud.java.jautohash.agent;

import java.lang.instrument.Instrumentation;

public class InstrumentationUtils {
    public static boolean equals(Object thisObject, Object o) {
        return false;
    }

    public static int hashCode(Object thisObject) {
        AgentInitialization.initialize();
        try {
            Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
            final Class<?> clazz = thisObject.getClass();
            HashCodeTransformer transformer = new HashCodeTransformer(clazz);
            instrumentation.addTransformer(transformer, true);
            instrumentation.retransformClasses(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return thisObject.hashCode();
    }
}