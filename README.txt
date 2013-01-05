StringTemplate 4.0.7
January 4, 2013

Terence Parr, parrt at cs usfca edu
ANTLR project lead and supreme dictator for life
University of San Francisco

ST (StringTemplate) is a java template engine (with ports for C#,
Python, and Objective-C coming) for generating source code, web pages,
emails, or any other formatted text output. ST is particularly good at
multi-targeted code generators, multiple site skins, and
internationalization / localization. It evolved over years of effort
developing jGuru.com and then ANTLR v3. The main website is:

  http://www.stringtemplate.org

Its distinguishing characteristic is that it strictly enforces
model-view separation, unlike other engines. See:

  http://www.cs.usfca.edu/~parrt/papers/mvc.templates.pdf

The documentation is in the wiki:

  http://www.antlr.org/wiki/display/ST4/StringTemplate+4+Documentation

Per the BSD license in LICENSE.txt, this software is not guaranteed to
work and might even destroy all life on this planet.

See the CHANGES.txt file.

INSTALLATION

All you need to do is get the StringTemplate jar into your CLASSPATH
as well as its dependent ANTLR jar. Download the following and put
into your favorite lib directory such as /usr/local/lib on UNIX:

  * antlr-3.5-complete.jar; http://www.antlr.org/download/antlr-3.5-complete.jar
  * ST.jar; see http://www.stringtemplate.org/download/ST-4.0.7.jar

Add to your CLASSPATH. On UNIX that looks like

$ export CLASSPATH="/usr/local/lib/antlr-3.5-complete.jar:/usr/local/lib/ST-4.0.7.jar:$CLASSPATH"

Java will now see all the libraries necessary to execute ST stuff.

BUILDING FROM SOURCE

The source is at github.com:

	https://github.com/antlr/stringtemplate4

If you would like to make changes to ST and build it yourself, just set
build.properties to the appropriate version of this:

  version=4.0.7
  antlr3.jar=/usr/local/lib/antlr-3.5-rc-2-complete.jar
  build.sysclasspath=ignore

and then run "ant" from the main directory.

Then, once you're set up with the ant task, go for it. Looks like this:

$ cd /usr/local/ST-4.0.7
$ ant
Buildfile: /Users/parrt/antlr/code/stringtemplate4/build.xml

clean:
   [delete] Deleting directory /Users/parrt/antlr/code/stringtemplate4/build

init:
    [mkdir] Created dir: /Users/parrt/antlr/code/stringtemplate4/build/generated-sources/antlr3
    [mkdir] Created dir: /Users/parrt/antlr/code/stringtemplate4/build/classes

antlr:
     [echo] Run ANTLR on grammars
     [java] ANTLR Parser Generator  Version 3.x
     [java] Output file /Users/parrt/antlr/code/stringtemplate4/build/generated-sources/antlr3/org/stringtemplate/v4/compiler/STParser.java does not exist: must build /Users/parrt/antlr/code/stringtemplate4/src/org/stringtemplate/v4/compiler/STParser.g
     [java] STParser.g
     [java] Output file /Users/parrt/antlr/code/stringtemplate4/build/generated-sources/antlr3/org/stringtemplate/v4/compiler/GroupParser.java does not exist: must build /Users/parrt/antlr/code/stringtemplate4/src/org/stringtemplate/v4/compiler/Group.g
     [java] Group.g
     [java] Output file /Users/parrt/antlr/code/stringtemplate4/build/generated-sources/antlr3/org/stringtemplate/v4/compiler/CodeGenerator.java does not exist: must build /Users/parrt/antlr/code/stringtemplate4/src/org/stringtemplate/v4/compiler/CodeGenerator.g
     [java] CodeGenerator.g

compile:
    [javac] Compiling 61 source files to /Users/parrt/antlr/code/stringtemplate4/build/classes

zip-source:
    [mkdir] Created dir: /Users/parrt/antlr/code/stringtemplate4/dist/ST-4.0.7
    [mkdir] Created dir: /Users/parrt/antlr/code/stringtemplate4/dist/ST-4.0.7/src
     [copy] Copying 68 files to /Users/parrt/antlr/code/stringtemplate4/dist/ST-4.0.7/src
     [copy] Copying 5 files to /Users/parrt/antlr/code/stringtemplate4/dist/ST-4.0.7
     [copy] Copying 1 file to /Users/parrt/antlr/code/stringtemplate4/dist/ST-4.0.7/lib
      [zip] Building zip: /Users/parrt/antlr/code/stringtemplate4/dist/ST-4.0.7-src.zip

build-jar:
      [jar] Building jar: /Users/parrt/antlr/code/stringtemplate4/dist/ST-4.0.7.jar

distribute:

BUILD SUCCESSFUL
Total time: 4 seconds
