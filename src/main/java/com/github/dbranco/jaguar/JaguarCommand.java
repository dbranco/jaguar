package com.github.dbranco.jaguar;

import com.github.dbranco.jaguar.subcommands.JaguarInstall;
import com.github.dbranco.jaguar.subcommands.JaguarList;

import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "jaguar", version = "1.0.0",
    description = "Manage multiple JVM installations on a Windows computer.",
    subcommands = {
            JaguarList.class,
            JaguarInstall.class
        }
    )
@Component
public class JaguarCommand implements Runnable {

    @Option(names = {"-h", "--help"}, usageHelp = true)
    private boolean help;

    @Override
    public void run() {
        CommandLine cmd = new CommandLine(this);
        cmd.usage(System.out);
    }
    
}
