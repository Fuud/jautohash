package com.blogspot.fuud.java.jautohash.agent;


import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class HashCodeTransformer implements ClassFileTransformer {
    private String classToInstrument;

    public HashCodeTransformer(Class<?> clazz) {
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

    private void setBody(CtClass clazz, CtMethod hashCodeMethod) throws CannotCompileException {
        StringBuilder body = new StringBuilder("{");
        body.append("System.out.println(\"It works!!!\");\n");
        body.append(" int result = 0;\n");
        for (CtField ctField : clazz.getFields()) {
            String asObject = "((w)" + ctField.getName() + ")";
            body.append("result = 31 * result + " + asObject + " != null ? " + asObject + ".hashCode() : 0);\n");
        }
        body.append("return result;");
        body.append("}");
        hashCodeMethod.setBody(body.toString());
    }
}
