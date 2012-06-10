package com.blogspot.fuud.java.jautohash.agent;


import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class EqualsTransformer implements ClassFileTransformer {
    private String classToInstrument;

    public EqualsTransformer(Class<?> clazz) {
        this.classToInstrument = clazz.getName().replace('.', '/');
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.equals(classToInstrument)) {
            return null;
        }

        try {
            return transformImpl(classfileBuffer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private byte[] transformImpl(byte[] classfileBuffer) throws IOException, NotFoundException, CannotCompileException {
        CtClass clazz = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));
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

    private void setBody(CtClass clazz, CtMethod hashCodeMethod) throws CannotCompileException {
        final String clazzName = clazz.getName();

        String bodyS = String.format("" +
                "{" +
                "   System.out.println(\"will be compared: \"+this+\" and \"+ $1);" +
                "   if (this == $1) return true;" +
                "   if (!($1 instanceof %s)) return false;" +
                "   %s target = (%s) $1;",
                clazzName, clazzName, clazzName);

        StringBuilder body = new StringBuilder(bodyS);
        for (CtField ctField : clazz.getDeclaredFields()) {
            final String asObject = "(($w)" + ctField.getName() + ")";
            final String asObjectTarget = "(($w)target." + ctField.getName() + ")";

            body.append(format("" +
                    "        System.out.println(\"will be compared: \"+%(thisField)+\" and \"+ %(thatField)); \n" +
                    "        if (%(thisField) != null) {\n" +
                    "            if (! %(thisField).equals(%(thatField)) ) return false;\n" +
                    "            " +
                    "        } else {\n" +
                    "            if (%(thatField) != null) return false;\n" +
                    "        }",
                    new HashMap<String, Object>() {{
                        put("thisField", asObject);
                        put("thatField", asObjectTarget);
                    }}));
        }
        body.append("return true;\n");
        body.append("}");
        hashCodeMethod.setBody(body.toString());
    }

    public static String format(String str, Map<String, Object> values) {

        StringBuilder builder = new StringBuilder(str);

        for (Map.Entry<String, Object> entry : values.entrySet()) {

            int start;
            String pattern = "%(" + entry.getKey() + ")";
            String value = entry.getValue().toString();

            // Replace every occurence of %(key) with value
            while ((start = builder.indexOf(pattern)) != -1) {
                builder.replace(start, start + pattern.length(), value);
            }
        }

        return builder.toString();
    }

}
