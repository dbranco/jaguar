package com.github.dbranco.jaguar.subcommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.dbranco.jaguar.JaguarCommand;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "list", aliases = {
        "ls" }, description = "[available] List the JDK installations. Type \"available\" at the end to see what can be installed.")
@Component
public class JaguarList implements Runnable {

    @Parameters(defaultValue = "", description = "The listing type. \"Availabe\" is used to list the remote available JDKs.")
    private String type;

    public void run() {
        
        if ("".equalsIgnoreCase(type)) {
            listLocal();
            return;
        }else if ("available".equalsIgnoreCase(type)) {
            listRemote();
            return;
        }
        
        CommandLine cmd = new CommandLine(this);
        cmd.usage(System.out);

    }

    private void listRemote() {
        WebClient client = WebClient.builder()
            .baseUrl("https://raw.githubusercontent.com/dbranco/jaguar/main/src/main/resources/list.json")
            .build();

        String bodyToMono = client.get()
            .retrieve()
            .bodyToMono(String.class)
            .block();

        System.out.println("Hello LS" + bodyToMono);
    }

    private void listLocal() {
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
