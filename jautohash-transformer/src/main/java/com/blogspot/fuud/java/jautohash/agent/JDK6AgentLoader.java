package com.blogspot.fuud.java.jautohash.agent;

import com.sun.tools.attach.VirtualMachine;

import java.lang.management.ManagementFactory;

public final class JDK6AgentLoader {

    private final String jarFilePath;
    private final String pid;

    public JDK6AgentLoader(String jarFilePath) {
        this.jarFilePath = jarFilePath;
        pid = discoverProcessIdForRunningVM();
    }

    public void loadAgent() {
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarFilePath);
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String discoverProcessIdForRunningVM() {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        return nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));
    }
}
