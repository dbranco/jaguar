package com.github.dbranco.jaguar.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The service to list the JDKs locally installed.
 */
@Service
public class JdkListLocalService implements JdkListService<String> {
    private static final String NO_JDK_LOCALLY_INSTALLED = "There are no JDK locally installed.";
    private static final Logger LOGGER = LoggerFactory.getLogger(JdkListLocalService.class);

    public List<String> list() {
        Path jdkInstallations = Path.of("jdkInstallations");

        if (!Files.exists(jdkInstallations) || !Files.isDirectory(jdkInstallations)) {
            return Collections.emptyList();
        }

        try {
            return Files.list(jdkInstallations)
                .filter(aJdkInstallation -> Files.isDirectory(aJdkInstallation))
                .map(aJdkInstallation -> aJdkInstallation.getFileName().toString())
                .collect(Collectors.toList());
        } catch (IOException theCause) {
            LOGGER.error("An error while has ocurred while listing the locally installed JDKs", theCause);
        }

        return Collections.emptyList();
    }

    @Override
    public void print(List<String> theOutputToRender) {
        if (theOutputToRender == null || theOutputToRender.isEmpty()) {
            System.out.println(NO_JDK_LOCALLY_INSTALLED);
        } else {
            System.out.println(theOutputToRender);
        }
    }

}
