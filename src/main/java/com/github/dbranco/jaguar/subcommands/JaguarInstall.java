package com.github.dbranco.jaguar.subcommands;

import com.github.dbranco.jaguar.service.JdkInstallationService;
import com.github.dbranco.jaguar.service.JdkInstallationService.InstallationParameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "install", 
        description = "<version> [arch] The version can be a JDK version or \"latest\" for the latest stable version. "
        + "Optionally specify whether to install the 32 or 64 bit version (defaults to system arch). "
        + "Set [arch] to \"all\" to install 32 AND 64 bit versions.")
@Component
public class JaguarInstall implements Runnable {

    @Parameters
    private String version;

    @Autowired
    JdkInstallationService installService;

    @Override
    public void run() {
        // FIXME need to take into account the architecture and provider
        var allParameters = new InstallationParameter("windows", "x64", "", version);
        installService.install(allParameters);
    }

}
