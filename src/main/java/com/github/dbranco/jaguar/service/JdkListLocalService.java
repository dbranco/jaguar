package com.github.dbranco.jaguar.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

@Service
public class JdkListLocalService implements JdkListService {

    public void list() {
        Path jdkInstallations = Path.of("jdkInstallations");

        if (!Files.exists(jdkInstallations) || !Files.isDirectory(jdkInstallations)) {
            System.out.println("There are no JDK locally installed.");
            return;
        }

        try {
            Files.list(jdkInstallations)
                .filter(aJdkInstallation -> Files.isDirectory(aJdkInstallation))
                .forEach(aJdkInstallation -> System.out.println(aJdkInstallation.getFileName()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
