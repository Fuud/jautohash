package com.blogspot.fuud.java.jautohash.agent;


import com.blogspot.fuud.java.jautohash.agent.util.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

public class MyClassFileTransformer implements ClassFileTransformer {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private String classToInstrument;
    private CtClass replacementHolder;
    private CtClass ctClassNewInstanceToReplace;

    public MyClassFileTransformer(String classToInstrument, String classNewInstanceToReplace, Object newInstance) {
        this.classToInstrument = classToInstrument.replace('.', '/');

        try {

            ctClassNewInstanceToReplace = ClassPool.getDefault().get(classNewInstanceToReplace);

            replacementHolder = ClassPool.getDefault().getAndRename(Singleton.class.getName(), Singleton.class.getName() + COUNTER.incrementAndGet());
            System.out.println(ctClassNewInstanceToReplace.getConstructors().length);
            for (CtConstructor ctConstructor : ctClassNewInstanceToReplace.getConstructors()) {
                CtMethod ctGetter = new CtMethod(ctClassNewInstanceToReplace, "getReplacement", ctConstructor.getParameterTypes(), replacementHolder);
                ctGetter.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
                ctGetter.setBody("return ($r) getClazz();");

                replacementHolder.addMethod(ctGetter);
            }

            Class staticClass = replacementHolder.toClass();
            Method setter = staticClass.getMethod("setClazz", Object.class);
            setter.invoke(null, newInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] transform(ClassLoader loader, String className, java.lang.Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.equals(classToInstrument)) {
            return null;
        }

        try {
            return transformImpl(classfileBuffer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] transformImpl(byte[] classfileBuffer) throws IOException, NotFoundException, CannotCompileException {
        CtClass testedClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));

        CodeConverter converter = new CodeConverter();
        converter.replaceNew(ctClassNewInstanceToReplace, replacementHolder, "getReplacement");
        testedClass.instrument(converter);
        final byte[] bytes = testedClass.toBytecode();
        testedClass.defrost();
        return bytes;
    }
}
