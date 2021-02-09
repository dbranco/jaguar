package com.github.dbranco.jaguar.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The service to list the JDKs locally installed.
 */
@Service
public class JdkListLocalService implements JdkListService {
    private static final String NO_JDK_LOCALLY_INSTALLED = "There are no JDK locally installed.";
    private static final Logger LOGGER = LoggerFactory.getLogger(JdkListLocalService.class);

    public void list() {
        Path jdkInstallations = Path.of("jdkInstallations");

        if (!Files.exists(jdkInstallations) || !Files.isDirectory(jdkInstallations)) {
            System.out.println(NO_JDK_LOCALLY_INSTALLED);
            return;
        }

        try {

            boolean isAtLeastOneDirectory = false;
            for (Path aJdkInstallation : Files.newDirectoryStream(jdkInstallations)) {
                if (Files.isDirectory(aJdkInstallation)){
                    isAtLeastOneDirectory = false;
                    System.out.println(aJdkInstallation.getFileName());
                }
            }

            if (!isAtLeastOneDirectory) {
                System.out.println(NO_JDK_LOCALLY_INSTALLED);
                return;
            }

            } catch (IOException theCause) {
            LOGGER.error("An error while has ocurred while listing the locally installed JDKs", theCause);
        }
    }

}
