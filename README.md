# AoseDevKit2018

The AoseDevKit2018 framework has been implemented for the course of Agent Oriented Software Engineering at the University Of Trento (2018).
The framework consists of three repositories:
- *AoseDevKit2018-MultiAgentSystem: https://github.com/marcorobol/AoseDevKit2018-MultiAgentSystem*
- *AoseDevKit2018-Generic: https://github.com/marcorobol/AoseDevKit2018-Generic*
- *AoseDevKit2018-Blocksworld: https://github.com/marcorobol/AoseDevKit2018-Blocksworld*

Introductory slides on the framework are available in the doc folder in the AoseDevKit2018-MultiAgentSystem repository.

In the case of bugs to any of these, let us know or fix them and do a pull request.

## Installing and running

Prerequisites: Git + Eclipse + JDK 1.8

This repository depends on *AoseDevKit2018-Generic* and *AoseDevKit2018-MultiAgentSystem*
so be sure to import all of them into your Eclipse workspace.
To do so follow these steps:

1. Fork and clone all the 3 repositories:
    > $ git clone xxx
2. Import the projects in Eclipse:
    > File -> Import -> Existing Projects into Workspace
3. **[Only for Linux/Mac users]** Give execution permission to blackbox executable in 2018-AoseDevKit-Blocksworld/blackbox:
    > $ chmod +x blackbox
4. **[Only if you have multiple versions of Java]** In the case Eclipse detects errors in the projects
    - in the Eclipse preferences set default compiler to Java 1.8
    - or right click on each of them and select:
      > Properties -> Java Compiler -> Enable project specific setting -> Compiler 1.8
  or 
5. To run right click on file unitn.adk2018.blocksworld.BlocksworldLauncher and select:
    > Run as -> Java Application

# AoseDevKit2018-Generic
