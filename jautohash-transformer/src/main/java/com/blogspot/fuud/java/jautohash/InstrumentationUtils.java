package com.blogspot.fuud.java.jautohash;

import com.blogspot.fuud.java.jautohash.agent.AgentInitialization;
import com.blogspot.fuud.java.jautohash.agent.EqualsTransformer;
import com.blogspot.fuud.java.jautohash.agent.HashCodeTransformer;
import com.blogspot.fuud.java.jautohash.agent.InstrumentationHolder;

import java.lang.instrument.Instrumentation;

public class InstrumentationUtils {
    public static boolean equals(Object thisObject, Object o) {
        AgentInitialization.initialize();
        try {
            Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
            final Class<?> clazz = thisObject.getClass();
            EqualsTransformer transformer = new EqualsTransformer(clazz);
            instrumentation.addTransformer(transformer, true);
            instrumentation.retransformClasses(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return thisObject.equals(o);
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
