package com.github.dbranco.jaguar.subcommands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.dbranco.jaguar.service.JdkListLocalService;
import com.github.dbranco.jaguar.service.JdkListRemoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "list", aliases = {
        "ls" }, description = "[available] List the JDK installations. Type \"available\" at the end to see what can be installed.")
@Component
public class JaguarList implements Runnable {

    @Parameters(defaultValue = "", description = "The listing type. \"Availabe\" is used to list the remote available JDKs.")
    private String type;

    @Autowired
    private JdkListLocalService local;

    @Autowired
    private JdkListRemoteService remote;

    public void run() {
        
        if ("".equalsIgnoreCase(type)) {
            local.list();
            return;
        }else if ("available".equalsIgnoreCase(type)) {
            remote.list();
            return;
        }
        
        CommandLine cmd = new CommandLine(this);
        cmd.usage(System.out);
    }

    

    
    
}
