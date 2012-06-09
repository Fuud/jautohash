package com.blogspot.fuud.java.jautohash.agent;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.regex.Pattern;

public final class AgentInitialization {
    private static final String javaSpecVersion = System.getProperty("java.specification.version");
    private static final boolean jdk6OrLater = "1.6".equals(javaSpecVersion) || "1.7".equals(javaSpecVersion);
    private static final Pattern JAR_REGEX = Pattern.compile(".*new-instance-transformer[-.\\d]*.jar");

    private static boolean initialized = false;

    public synchronized static void initialize() {
        if (initialized){
            return;
        }
        initialized = true;

        String jarFilePath = discoverPathToJarFile();

        if (jdk6OrLater) {
            new JDK6AgentLoader(jarFilePath).loadAgent();
        } else{
            throw new IllegalStateException("JDK should be 1.6 or later to support ");
        }
    }

    private static String discoverPathToJarFile() {
        String jarFilePath = findPathToJarFileFromClasspath();

        if (jarFilePath == null) {
            // This can fail for a remote URL, so it is used as a fallback only:
            jarFilePath = getPathToJarFileContainingThisClass();
        }

        if (jarFilePath != null) {
            return jarFilePath;
        }

        throw new IllegalStateException(
                "No jar file with name ending in \"jmockit.jar\" or \"jmockit-nnn.jar\" (where \"nnn\" " +
                        "is a version number) found in the classpath");
    }

    private static String findPathToJarFileFromClasspath() {
        String[] classPath = System.getProperty("java.class.path").split(File.pathSeparator);

        for (String cpEntry : classPath) {
            if (JAR_REGEX.matcher(cpEntry).matches()) {
                return cpEntry;
            }
        }

        return null;
    }

    private static String getPathToJarFileContainingThisClass() {
        CodeSource codeSource = InstrumentationHolder.class.getProtectionDomain().getCodeSource();

        if (codeSource == null) {
            return null;
        }

        URI jarFileURI; // URI is needed to deal with spaces and non-ASCII characters

        try {
            jarFileURI = codeSource.getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return new File(jarFileURI).getPath();
    }
}