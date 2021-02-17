package com.github.dbranco.jaguar.component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DecompressJdkProcess {

  private static final Logger LOGGER = LoggerFactory.getLogger(DecompressJdkProcess.class);

  /**
   * This method is used to get the tar file name from the gz file by removing the
   * .gz part from the input file
   * 
   * @param theInputFile
   * @param theOutputFolder
   */
  public void getFileName(File theInputFile, String theOutputFolder) {

    try (InputStream aSourceFileInputStream = Files.newInputStream(theInputFile.toPath());
        ArchiveInputStream aArchiveInputStream = new ArchiveStreamFactory()
            .createArchiveInputStream(new BufferedInputStream(aSourceFileInputStream))) {

      ArchiveEntry aArchiveEntry = null;
      while ((aArchiveEntry = aArchiveInputStream.getNextEntry()) != null) {
        if (!aArchiveInputStream.canReadEntryData(aArchiveEntry)) {
          LOGGER.warn("There are an element ({}) that can't be read", aArchiveEntry.getName());
          continue;
        }
        String aDestinationFilename = theOutputFolder + aArchiveEntry.getName();
        File aOutputFile = new File(aDestinationFilename);
        if (aArchiveEntry.isDirectory()) {
          if (!aOutputFile.isDirectory() && !aOutputFile.mkdirs()) {
            throw new IOException("Failed to create directory " + aOutputFile);
          }
        } else {
          File aParentFile = aOutputFile.getParentFile();
          if (!aParentFile.isDirectory() && !aParentFile.mkdirs()) {
            throw new IOException("Failed to create directory " + aParentFile);
          }
          try (OutputStream aOutputFileStream = Files.newOutputStream(aOutputFile.toPath())) {
            IOUtils.copy(aArchiveInputStream, aOutputFileStream);
          }
        }
      }

    } catch (IOException | ArchiveException theException) {
      LOGGER.error("Error while trying to opening the file.", theException);
    }

  }

}
