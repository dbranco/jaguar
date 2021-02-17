package com.github.dbranco.jaguar;

import com.github.dbranco.jaguar.config.JaguarConfigurationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
@EnableConfigurationProperties(JaguarConfigurationProperties.class)
public class JaguarApplication implements CommandLineRunner, ExitCodeGenerator {

  @Autowired
  private JaguarCommand mainCommand;

  @Autowired
  private IFactory factory;
  private int exitCode;

  @Override
  public void run(String... args) {
    // let picocli parse command line args and run the business logic
    exitCode = new CommandLine(mainCommand, factory).execute(args);
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }

  public static void main(String[] args) {
    // let Spring instantiate and inject dependencies
    System.exit(SpringApplication.exit(SpringApplication.run(JaguarApplication.class, args)));
  }

  @Bean
  public String appDirectory() {
    String appDirectory = System.getProperty("appDirectory");
    return appDirectory == null ? "./bin" : appDirectory;
  }

}