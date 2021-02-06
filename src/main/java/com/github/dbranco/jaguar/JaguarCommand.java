package com.github.dbranco.jaguar;

import com.github.dbranco.jaguar.subcommands.JaguarList;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Command(
    name = "jaguar", version = "1.0.0",
    description = "Manage multiple JVM installations on a Windows computer.",
    subcommands = {
            JaguarList.class
        }
    )
@Component
public class JaguarCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Hello From Jaguar");
    }
    
}
