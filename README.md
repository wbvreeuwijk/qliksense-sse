# qs-aai-java

Example implementation of Server Side Extensions for Qlik Sense using Java.

## Introduction

Java based implementation of Regular Expression transformation in Qlik Sense. This extension provides two functions 
- RegExp(*Pattern*, *Replacement*, *String*) and returns a string for each input row
- RegExpAgr(*Pattern*, *Replacement*, *String*) and returns a concatenated string for all rows


 ## Building
 
This application was built using the Gradle Build Tool. To build the application you will need to have a working gradle installation in your environment. Go to [gradle](https://gradle.org/) to learn more about running the Gradle build tool.

## Running

Download the zip-file from the release section. This is a self-contained environment that has all the needed components and only needs a Java Runtime. Unzip the file in a directory and start with
- **Windows**: `bin\aai-regexp-server.bat`
- **Linux**: `bin/aai-regexp-server`

## TODO

- [ ] Javadoc documentation
- [ ] Separate framework from implementation  