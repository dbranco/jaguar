package com.github.dbranco.jaguar;

import java.nio.file.FileSystems;
import java.nio.file.Path;

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

  @Bean
  public Path jdkTmpFolder() {
    final String TMP_DIRECTORY = "tmp";
    var aPath = FileSystems.getDefault().getPath(appDirectory(), TMP_DIRECTORY);
    if (!(aPath.toFile().exists() || aPath.toFile().isDirectory())) {
      aPath.toFile().mkdirs();
    }
    
    return aPath;
  }
  
  @Bean
  public Path jdkInstallationFolder() {
    final String INSTALL_DIRECTORY = "install";
    var aPath = FileSystems.getDefault().getPath(appDirectory(), INSTALL_DIRECTORY);
    if (!(aPath.toFile().exists() || aPath.toFile().isDirectory())) {
      aPath.toFile().mkdirs();
    }

    return aPath;
  }

}