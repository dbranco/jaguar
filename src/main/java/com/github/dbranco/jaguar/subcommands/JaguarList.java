package com.github.dbranco.jaguar.subcommands;

import com.github.dbranco.jaguar.service.JdkListLocalService;
import com.github.dbranco.jaguar.service.JdkListRemoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "list",
        aliases = {"ls" },
        description = "[available] List the JDK installations. Type \"available\" at the end to see what can be installed."
        )
@Component
public class JaguarList implements Runnable {

    private static final String REMOTE_TYPE = "available";
    private static final String LOCAL_TYPE = "";

    @Parameters(defaultValue = LOCAL_TYPE, description = "The listing type. \"Availabe\" is used to list the remote available JDKs.")
    private String type;

    @Autowired
    private JdkListLocalService local;

    @Autowired
    private JdkListRemoteService remote;

    public void run() {
        if (LOCAL_TYPE.equalsIgnoreCase(type)) {
            local.listAndPrint();
            return;
        } else if (REMOTE_TYPE.equalsIgnoreCase(type)) {
            remote.listAndPrint();
            return;
        }

        CommandLine cmd = new CommandLine(this);
        cmd.usage(System.out);
    }

}
