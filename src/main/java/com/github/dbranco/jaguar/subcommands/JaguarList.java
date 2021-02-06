package com.github.dbranco.jaguar.subcommands;

import org.springframework.stereotype.Component;

import picocli.CommandLine.Command;

@Command(name = "ls")
@Component
public class JaguarList implements Runnable {

    public void run() {
        System.out.println("Hello LS");
    }
    
}
