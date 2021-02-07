package com.github.dbranco.jaguar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import picocli.CommandLine;

@SpringBootApplication
public class JaguarApplication implements CommandLineRunner {
  
  @Autowired
  private JaguarCommand mainCommand;
  
  @Override
  public void run(String... args) throws Exception {
    new CommandLine(mainCommand)
      .execute(args);

  }
  
  public static void main(String[] args) {
    SpringApplication.run(JaguarApplication.class, args);
  }
  
}