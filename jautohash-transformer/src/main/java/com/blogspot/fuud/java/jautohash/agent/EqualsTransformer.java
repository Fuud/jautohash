package com.blogspot.fuud.java.jautohash.agent;


import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;

import static com.blogspot.fuud.java.jautohash.agent.util.StrUtils.format;

public class EqualsTransformer implements ClassFileTransformer {
    private String classToInstrument;

    public EqualsTransformer(Class<?> clazz) {
        this.classToInstrument = clazz.getName().replace('.', '/');
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        if (!className.equals(classToInstrument)) {
            return null;
        }

        try {
            return transformImpl(classFileBuffer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private byte[] transformImpl(byte[] classFileBuffer) throws IOException, NotFoundException, CannotCompileException {
        CtClass clazz = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classFileBuffer));
        final CtClass javaLangObject = ClassPool.getDefault().get("java.lang.Object");

        CtMethod hashCodeMethod;
        try {
            hashCodeMethod = clazz.getDeclaredMethod("equals", new CtClass[]{javaLangObject});
            setBody(clazz, hashCodeMethod);
        } catch (NotFoundException e) {
            hashCodeMethod = new CtMethod(ClassPool.getDefault().get(int.class.getName()), "equals", new CtClass[]{javaLangObject}, clazz);
            setBody(clazz, hashCodeMethod);
            clazz.addMethod(hashCodeMethod);
        }

        final byte[] bytes = clazz.toBytecode();
        clazz.defrost();
        return bytes;
    }

    private void setBody(CtClass clazz, CtMethod hashCodeMethod) throws CannotCompileException, NotFoundException {
        final String clazzName = clazz.getName();

        String bodyS = String.format("" +
                "{" +
                "   if (this == $1) return true;" +
                "   if (!($1 instanceof %s)) return false;" +
                "   %s target = (%s) $1;",
                clazzName, clazzName, clazzName);

        StringBuilder body = new StringBuilder(bodyS);
        for (CtField ctField : clazz.getDeclaredFields()) {
            final String thisField = ctField.getName();
            final String thatField = "target." + ctField.getName();

            if (ctField.getType().isPrimitive()) {
                body.append(
                        format("if (%(thisField) != %(thatField)) return false;\n",
                        new HashMap<String, Object>() {{
                            put("thisField", thisField);
                            put("thatField", thatField);
                        }}));
            } else {
                body.append(format("" +
                        "        if (%(thisField) != null) {\n" +
                        "            if (! %(thisField).equals(%(thatField)) ) return false;\n" +
                        "            " +
                        "        } else {\n" +
                        "            if (%(thatField) != null) return false;\n" +
                        "        }",
                        new HashMap<String, Object>() {{
                            put("thisField", thisField);
                            put("thatField", thatField);
                        }}));
            }
        }
        body.append("return true;\n");
        body.append("}");
        hashCodeMethod.setBody(body.toString());
    }

}
