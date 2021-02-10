package com.github.dbranco.jaguar.service;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.file.FileSystems;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    JdkListRemoteService remoteList;

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

    private Mono<Void> write(Publisher<DataBuffer> source, Path destination, OpenOption... options) {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(destination, "Destination must not be null");

		Set<OpenOption> optionSet = createFileOptions();

		return Mono.create(sink -> {
			try {
				AsynchronousFileChannel channel = AsynchronousFileChannel.open(destination, optionSet, null);
				sink.onDispose(() -> closeChannel(channel));
				DataBufferUtils.write(source, channel).subscribe(
                    // TODO Do some process to update the console output
                    someBufferInput -> {
                        System.out.println(someBufferInput);
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

    

    private void downloadArchive(JdkArchive aArchive) {
        WebClient aClient = WebClient.builder()
            .baseUrl(aArchive.getUrl())
            .build();
        
            
        var aFileToDownload = aArchive.getUrl().substring(aArchive.getUrl().lastIndexOf("/") + 1);
        var aDataBuffer = aClient.get().retrieve().bodyToFlux(DataBuffer.class);
        var aPath = FileSystems.getDefault().getPath(aFileToDownload);
        write(aDataBuffer, aPath, StandardOpenOption.CREATE)
            // TODO extract the downloaded file in here
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
    
}
