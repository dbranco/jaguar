# jaguar
A java version manager like NVM for node. This is a project written in Java for Java Community.

## Why Jaguar?
Digging around I haven't been able to find a solution that satisfies me. So I decided to create "jaguar".

The project is intended to create a full package deliverable that can list, download and configures the JDK to be used.

This project will also help me to practice a little about some concepts, like using Spring+SpringBoot, gradle for build, using jpackage to create a deliverable, etc...

## Dependencies for development
I am trying to have this project with less dependencies needed as possible, but since I am using jpackage you will require at least to have a JDK with this tool (JDK 14).

## What Next
Since I have created the functional structure of the project and seems to work as a fully deliverable, I will open a series of task to create the functionality and let people visualise the progress of the project.

For now a simple "list" command is implemented and it fakely list your local machine and a file that contains the location and version of the JDKs.

My next goal is to really implement that "list" function, follow by:
- "install" will download the JDK and unzip it
- "use" will
    - Create, if does't exist, a shortcut that will always point to the active JDK
    - Create a JAVA_HOME if it doesn't exist and update it to the shortcut
    - Add JAVA_HOME to the path if it is not added yet
- Probably create GitHub pipelines for CI/CD
- Add an installation file that will create a JAGUAR_HOME and add it to the PATH
- Another interesting step would be to create a installer for chocolatey, so it can be easily installed using this application
- Improve the modularity of the deliverable. I am now packing the whole JRE and Spring; but hope some day to fully understand how I can modularise the project and its dependencies; or use tools like badass-jlink to do it.

## Last words!!
I hope to bring your attention, ideas, and contribution!! Happy coding :)