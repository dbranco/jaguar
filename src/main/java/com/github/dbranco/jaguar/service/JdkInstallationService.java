package com.github.dbranco.jaguar.service;

import com.github.dbranco.jaguar.model.JdkClassification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
public class JdkInstallationService {

    @Autowired
    JdkListRemoteService remoteList;

    private boolean matchParameters(JdkClassification theJdkClassification, InstallationParameter theParameters) {
        return theJdkClassification.getOs().equalsIgnoreCase(theParameters.getOs())
        && theJdkClassification.getArchitecture().equalsIgnoreCase(theParameters.getArchitecture())
        // && theJdkClassification.getProvider().equalsIgnoreCase(theParameters.getProvider())
        ;
    }

    public void install(InstallationParameter theParameters) {
        var aJdkToInstall = remoteList.list().stream()
            .filter(aJdkInstallation -> matchParameters(aJdkInstallation, theParameters))
            .flatMap(aJdkInstallation -> aJdkInstallation.getArchives().stream())
            .filter(aJdkArchive -> aJdkArchive.getVersion().equalsIgnoreCase(theParameters.getVersion()))
            .findFirst();
    }

    @Data
    public static class InstallationParameter {
        private final String os;
        private final String architecture;
        private final String provider;
        private final String version;
    }
    
}
