package com.github.dbranco.jaguar.service;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.file.FileSystems;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.dbranco.jaguar.component.DecompressJdkProcess;
import com.github.dbranco.jaguar.model.JdkClassification;
import com.github.dbranco.jaguar.model.JdkClassification.JdkArchive;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Data;
import reactor.core.publisher.Mono;

@Service
public class JdkInstallationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdkListRemoteService.class);
    private static final double MEGA_BYTES = Math.pow(1024, 2);
    private static final String TMP_DIRECTORY = "tmp";

    @Autowired
    private String appDirectory;

    @Autowired
    private JdkListRemoteService remoteList;

    @Autowired
    private DecompressJdkProcess decompressor;

    private boolean matchParameters(JdkClassification theJdkClassification, InstallationParameter theParameters) {
        return theJdkClassification.getOs().equalsIgnoreCase(theParameters.getOs())
                && theJdkClassification.getArchitecture().equalsIgnoreCase(theParameters.getArchitecture())
        // FIXME implement the JDK providers; i.e zulu, openjdk, amazon,...
        // &&
        // theJdkClassification.getProvider().equalsIgnoreCase(theParameters.getProvider())
        ;
    }

    private static Set<OpenOption> createFileOptions() {
        return Stream.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE).collect(Collectors.toSet());
    }

    private void closeChannel(Channel channel) {
		if (channel != null && channel.isOpen()) {
			try {
				channel.close();
			}
			catch (IOException ignored) {
			}
		}
	}

    private Mono<Void> write(Publisher<DataBuffer> theSource, int theFileSize, Path theDestination) {
		Assert.notNull(theSource, "Source must not be null");
		Assert.notNull(theDestination, "Destination must not be null");

		var optionSet = createFileOptions();
        var currentDownloadedSize = new AtomicLong(0);
        var consoleHelper = new ConsoleHelper();

		return Mono.create(sink -> {
			try {
				AsynchronousFileChannel channel = AsynchronousFileChannel.open(theDestination, optionSet, null);
				sink.onDispose(() -> closeChannel(channel));
				DataBufferUtils.write(theSource, channel).subscribe(
                    // TODO Do some process to update the console output
                    someBufferInput -> {
                        var currentDownloadedSizeMB = currentDownloadedSize.addAndGet(someBufferInput.capacity())/MEGA_BYTES;                        
                        var currentPercentage = Double.valueOf((currentDownloadedSizeMB*100)/theFileSize).intValue();
                        consoleHelper.animate(String.valueOf(currentPercentage) + "% - " + theFileSize + "MB");
                        DataBufferUtils.release(someBufferInput);
                    },
					sink::error,
					sink::success);
			}
			catch (IOException ex) {
				sink.error(ex);
			}
		});
	}

    private void createTmpFolderIfNeeded() {
        var aPath = FileSystems.getDefault().getPath(appDirectory, TMP_DIRECTORY);
        if (!(aPath.toFile().exists() || aPath.toFile().isDirectory())) {
            aPath.toFile().mkdirs();
        }
    }

    private void downloadArchive(JdkArchive aArchive) {
        WebClient aClient = WebClient.builder()
            .baseUrl(aArchive.getUrl())
            .build();
        
            
        var aFileToDownload = aArchive.getUrl().substring(aArchive.getUrl().lastIndexOf("/") + 1);
        var aFileSize = aClient.head()
            .retrieve()
            .toEntity(String.class)
            .map(aResponse -> aResponse.getHeaders().getContentLength()/MEGA_BYTES)
            .block();

        var aDataBuffer = aClient.get().retrieve().bodyToFlux(DataBuffer.class);
        var aPath = FileSystems.getDefault().getPath(appDirectory, TMP_DIRECTORY, aFileToDownload);

        createTmpFolderIfNeeded();

        write(aDataBuffer, Double.valueOf(aFileSize).intValue(), aPath)
            .doAfterTerminate(() -> System.out.println("Extracting the file"))
            .block(); //Creates new file or overwrites exisiting file
    }

    public void install(InstallationParameter theParameters) {
        var aJdkToInstall = remoteList.list().stream()
                .filter(aJdkInstallation -> matchParameters(aJdkInstallation, theParameters))
                .flatMap(aJdkInstallation -> aJdkInstallation.getArchives().stream())
                .filter(aJdkArchive -> aJdkArchive.getVersion().equalsIgnoreCase(theParameters.getVersion()))
                .findFirst();

        if (aJdkToInstall.isPresent()) {
            downloadArchive(aJdkToInstall.get());
        }

    }

    @Data
    public static class InstallationParameter {
        private final String os;
        private final String architecture;
        private final String provider;
        private final String version;
    }
    
    private class ConsoleHelper {
        private String lastLine = "";
    
        public void print(String line) {
            //clear the last line if longer
            if (lastLine.length() > line.length()) {
                String temp = "";
                for (int i = 0; i < lastLine.length(); i++) {
                    temp += " ";
                }
                if (temp.length() > 1)
                    System.out.print("\r" + temp);
            }
            System.out.print("\r" + line);
            lastLine = line;
        }
    
        private byte anim;
    
        public void animate(String line) {
            switch (anim) {
                case 1:
                    print("[ \\ ] " + line);
                    break;
                case 2:
                    print("[ | ] " + line);
                    break;
                case 3:
                    print("[ / ] " + line);
                    break;
                default:
                    anim = 0;
                    print("[ - ] " + line);
            }
            anim++;
        }
    }
}
