# This is JeeSh 

[![Build Status](https://travis-ci.org/jeeshell/jeesh.svg?branch=master)](https://travis-ci.org/jeeshell/jeesh)

JeeSh stands for "JVM Extensible & Embeddable Shell". It aims at being a reboot of the famous
[Common Reusable SHell (CRaSH)](https://github.com/crashub/crash).
 
 
## Motivation

CRaSH is no longer maintained and [Spring Boot](https://projects.spring.io/spring-boot/) is removing support
for it with release 2.0. Having the ability to access Spring features through SSH is very handy, especially
for administrative tasks.


We envisioned three common use-cases:
* **Standalone shell**. Useful for encoding utilities
* **SSH Server**. If you want to have JeeSh running on your server providing functionality not available 
on a normal *nix shell or for embedding it into a [Spring](https://spring.io/) application (common use case for CRaSH)
* **REST Endpoints**. Allows you to expose your commands through a REST API, making it easy to create both 
a healthy repertoire of features and a client layer (other than SSH) to interact with it. The *examples* project
provides a sample on how to use [JQueryTerminal](http://terminal.jcubic.pl/) for this purpose.


# First Steps

You can check JeeSh in action right after checking out the project. The **examples** module is a Spring Boot app
that bootstraps JeeSh as both a SSH server and a REST API endpoint. Just run `./gradlew :examples:bootRun`
and navigate to [http://localhost:8080/](http://localhost:8080/) or run `ssh -p 2003 admin@localhost` (admin:admin).


Try the `help` command.
 

## First Command

JeeSh just takes advantage of [JCommander](http://jcommander.org/) for command specification. Here is
the entire code of the built in `hello` command
 
```java
@Parameters(commandNames = {"hello", "hi"} , commandDescription = "Just says hello")
public class Hello extends AbstractCommand {

    @Parameter(names = {"-n", "--name"}, description = "Your name")
    private String name;

    @Override
    public void execute(@NotNull CommandContext context) {
        context.println("Hello " + Optional.ofNullable(name).orElse("World"));
    }

}
```

The requirements:
* Annotate your command with `@Parameters`
* Implement `Command`. You will typically extend `net.je2sh.core.AbstractCommand` for convenience.


# Architecture

JeeSh is broken down into multiple modules, allowing users to only import what they need.
By design, and for experimentation purposes, it is built using Java 8 and Kotlin.


The base idea is that you can just define your commands using [JCommander](http://jcommander.org/) and those
will be automatically picked up by JeeSh (if [*annotations*](#annotations) module is used).


For parsing and rendering we use [jline3](https://github.com/jline/jline3), which allows using ANSI encodings
and overall makes life easier.


## Core

The core module provides the basis for everything else. Necessary interfaces and glue to interconnect
the different components that build JeeSh.

## Annotations

Enables *the magic* of interpreting `@Parameters`.


This module provides an annotation processor that creates a `CommandProvider` for you and makes it
available as a `ServiceProvider` (`META-INF/services`) which is how **core** loads the available commands.


Mode on this subject later on.


## Base Plugins

Small set of builtin plugins. Function mostly as an reference but also includes the `help` command.

## Shell

Standalone shell instant that can be executed from the terminal

## SSH

Pretty much self-explanatory.

## Spring (Boot)

When included this module autoconfigures JeeSh according the the properties you define. By default
both a SSH Server and a REST API will be enabled. These however can be controlled through the properties:
`jeesh.ssh.enabled` and `jeesh.rest.enabled` respectively (to be added on your `application.[properties|yaml]`).

# Disclaimer

This is definitely a work in progress so there is definitely a lot of room for improvement. If you want
to contribute go ahead and grab an issued or submitted your awesome new feature!

Licensed under the MIT License ([LICENSE](LICENSE.md))
