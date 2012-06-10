package com.blogspot.fuud.java.jautohash.agent;

import java.lang.instrument.Instrumentation;

public class InstrumentationHolder {
    private static volatile Instrumentation instrumentation;

    public static void premain(String agentArguments, Instrumentation instrumentation){
        InstrumentationHolder.instrumentation = instrumentation;
    }

    public static void agentmain(String agentArguments, Instrumentation instrumentation){
        premain(agentArguments, instrumentation);
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
