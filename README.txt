StringTemplate 4.0.3
June 21, 2011

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

  * antlr-complete.jar; http://www.antlr.org/download/antlr-3.3-complete.jar
  * ST.jar; see http://www.stringtemplate.org/download/ST-4.0.3.jar

Add to your CLASSPATH. On UNIX that looks like

$ export CLASSPATH="/usr/local/lib/antlr-3.3-complete.jar:/usr/local/lib/ST-4.0.3.jar:$CLASSPATH"

Java will now see all the libraries necessary to execute ST stuff.

BUILDING FROM SOURCE

If you would like to make changes to ST and build it yourself, just
run "ant" from the main directory. This assumes that you have the
antlr3 ant task set up so first take a look at:

http://www.antlr.org/wiki/display/ANTLR3/How+to+use+ant+with+ANTLR3

Then, once you're set up with the ant task, go for it. Looks like this:

$ cd /usr/local/ST-4.0.3
$ ant
Buildfile: /usr/local/website/st/depot/ST4/java/main/build.xml

clean:
   [delete] Deleting directory /usr/local/website/st/depot/ST4/java/main/build
   [delete] Deleting directory /usr/local/website/st/depot/ST4/java/main/dist

init:
    [mkdir] Created dir: /usr/local/website/st/depot/ST4/java/main/build/gen/org/stringtemplate/v4/compiler

antlr:

compile:
    [mkdir] Created dir: /usr/local/website/st/depot/ST4/java/main/build/classes
    [mkdir] Created dir: /usr/local/website/st/depot/ST4/java/main/build/src
     [copy] Copying 89 files to /usr/local/website/st/depot/ST4/java/main/build/src
    [javac] Compiling 57 source files to /usr/local/website/st/depot/ST4/java/main/build/classes
    [javac] Note: /usr/local/website/st/depot/ST4/java/main/build/src/org/stringtemplate/v4/StringRenderer.java uses or overrides a deprecated API.
    [javac] Note: Recompile with -Xlint:deprecation for details.
    [javac] Note: Some input files use unchecked or unsafe operations.
    [javac] Note: Recompile with -Xlint:unchecked for details.

zip-source:
    [mkdir] Created dir: /usr/local/website/st/depot/ST4/java/main/dist/ST-4.0.3
    [mkdir] Created dir: /usr/local/website/st/depot/ST4/java/main/dist/ST-4.0.3/src
     [copy] Copying 53 files to /usr/local/website/st/depot/ST4/java/main/dist/ST-4.0.3/src
     [copy] Copying 4 files to /usr/local/website/st/depot/ST4/java/main/dist/ST-4.0.3
     [copy] Copying 1 file to /usr/local/website/st/depot/ST4/java/main/dist/ST-4.0.3/lib
      [zip] Building zip: /usr/local/website/st/depot/ST4/java/main/dist/ST-4.0.3-src.zip

build-jar:
      [jar] Building jar: /usr/local/website/st/depot/ST4/java/main/dist/ST-4.0.3.jar

distribute:

BUILD SUCCESSFUL
Total time: 14 seconds

DAILY BUILDS

For the latest build, check out:

http://www.stringtemplate.org/depot/ST4/java/main/dist

It has both the source and the binary Java jar.
