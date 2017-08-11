# qliksense-sse

Example implementations of Server Side Extensions for Qlik Sense.

## Projects

The following projects are part of this repository

### qs-aai-java

Java based implementation of Regular Expression transformation in Qlik Sense. This extension provides two functions 
- RegExp(*Pattern*, *Replacement*, *String*) and returns a string for each input row
- RegExpAgr(*Pattern*, *Replacement*, *String*) and returns a concatenated string for all rows

The server process listens to 50053 and can be run using gradle

 