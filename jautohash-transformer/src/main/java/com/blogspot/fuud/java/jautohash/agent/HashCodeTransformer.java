package com.blogspot.fuud.java.jautohash.agent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Map;

import static com.blogspot.fuud.java.jautohash.agent.util.StrUtils.format;

public class HashCodeTransformer implements ClassFileTransformer {
    private String classToInstrument;

    public HashCodeTransformer(Class<?> clazz) {
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

        CtMethod hashCodeMethod;
        try {
            hashCodeMethod = clazz.getDeclaredMethod("hashCode");
            setBody(clazz, hashCodeMethod);
        } catch (NotFoundException e) {
            hashCodeMethod = new CtMethod(ClassPool.getDefault().get(int.class.getName()), "hashCode", new CtClass[0], clazz);
            setBody(clazz, hashCodeMethod);
            clazz.addMethod(hashCodeMethod);
        }

        final byte[] bytes = clazz.toBytecode();
        clazz.defrost();
        return bytes;
    }

    private void setBody(CtClass clazz, CtMethod hashCodeMethod) throws CannotCompileException, NotFoundException {
        StringBuilder body = new StringBuilder("{");
        body.append(" int result = 0;\n");
        for (CtField ctField : clazz.getDeclaredFields()) {
            body.append("result = 31 * result;");

            final Map<String, Object> fieldNameParam = Collections.singletonMap("fieldName", (Object) ctField.getName());
            if (ctField.getType().isPrimitive()) {
                //boolean, byte, char, short, int, long, float, double
                if (ctField.getType().getName().equals(boolean.class.getName())) {
                    body.append(
                            format("" +
                                    "if (%(fieldName)){" +
                                    "   result += 1;" +
                                    "}", fieldNameParam)
                    );
                } else if (ctField.getType().getName().equals(byte.class.getName()) ||
                        ctField.getType().getName().equals(char.class.getName()) ||
                        ctField.getType().getName().equals(short.class.getName()) ||
                        ctField.getType().getName().equals(int.class.getName())) {
                    body.append(format("result += %(fieldName);", fieldNameParam));
                } else if (ctField.getType().getName().equals(long.class.getName())) {
                    body.append(format("result += (int)(%(fieldName) ^ (%(fieldName) >>> 32));", fieldNameParam));
                } else if (ctField.getType().getName().equals(float.class.getName())) {
                    body.append(format("result += java.lang.Float.floatToIntBits(%(fieldName));", fieldNameParam));
                } else if (ctField.getType().getName().equals(double.class.getName())) {
                    body.append(format("" +
                            "result += com.blogspot.fuud.java.jautohash.agent.HashCodeTransformer.doubleHashCode(%(fieldName));",
                            fieldNameParam));
                }
            } else {
                body.append(
                        format("" +
                                "if (%(fieldName) != null){" +
                                "   result += %(fieldName).hashCode();" +
                                "}\n",
                                fieldNameParam));
            }
        }
        body.append("return result;");
        body.append("}");
        try {
            hashCodeMethod.setBody(body.toString());
        } catch (CannotCompileException e) {
            System.out.println("Can not compile body: \n" + body);
            throw e;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static int doubleHashCode(double value) {
        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }
}
