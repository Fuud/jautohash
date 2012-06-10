package com.blogspot.fuud.java.jautohash.agent;

import com.sun.tools.attach.*;
import com.sun.tools.attach.spi.AttachProvider;
import sun.tools.attach.BsdVirtualMachine;
import sun.tools.attach.LinuxVirtualMachine;
import sun.tools.attach.SolarisVirtualMachine;
import sun.tools.attach.WindowsVirtualMachine;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

final class JDK6AgentLoader
{
    private static final AttachProvider ATTACH_PROVIDER = new AttachProvider()
    {
        @Override
        public String name() { return null; }

        @Override
        public String type() { return null; }

        @Override
        public VirtualMachine attachVirtualMachine(String id) { return null; }

        @Override
        public List<VirtualMachineDescriptor> listVirtualMachines() { return null; }
    };

    private final String jarFilePath;
    private final String pid;

    JDK6AgentLoader(String jarFilePath)
    {
        this.jarFilePath = jarFilePath;
        pid = discoverProcessIdForRunningVM();
    }

    private String discoverProcessIdForRunningVM()
    {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');

        return nameOfRunningVM.substring(0, p);
    }

    boolean loadAgent()
    {
        VirtualMachine vm;

        if (AttachProvider.providers().isEmpty()) {
            vm = getVirtualMachineImplementationFromEmbeddedOnes();
        }
        else {
            vm = attachToThisVM();
        }

        if (vm != null) {
            loadAgentAndDetachFromThisVM(vm);
            return true;
        }

        return false;
    }

    @SuppressWarnings("UseOfSunClasses")
    private VirtualMachine getVirtualMachineImplementationFromEmbeddedOnes()
    {
        try {
            if (File.separatorChar == '\\') {
                return new WindowsVirtualMachine(ATTACH_PROVIDER, pid);
            }

            String osName = System.getProperty("os.name");

            if (osName.startsWith("Linux") || osName.startsWith("LINUX")) {
                return new LinuxVirtualMachine(ATTACH_PROVIDER, pid);
            }
            else if (osName.startsWith("Mac OS X")) {
                return new BsdVirtualMachine(ATTACH_PROVIDER, pid);
            }
            else if (osName.startsWith("Solaris")) {
                return new SolarisVirtualMachine(ATTACH_PROVIDER, pid);
            }
        }
        catch (AttachNotSupportedException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (UnsatisfiedLinkError e) {
            throw new IllegalStateException("Native library for Attach API not available in this JRE", e);
        }

        return null;
    }

    private VirtualMachine attachToThisVM()
    {
        try {
            return VirtualMachine.attach(pid);
        }
        catch (AttachNotSupportedException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAgentAndDetachFromThisVM(VirtualMachine vm)
    {
        try {
            vm.loadAgent(jarFilePath, null);
            vm.detach();
        }
        catch (AgentLoadException e) {
            throw new RuntimeException(e);
        }
        catch (AgentInitializationException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

