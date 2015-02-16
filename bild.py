#!/usr/bin/env python
import os
import string
import shutil

"""
This script uses my experimental build tool http://www.bildtool.org

cd antlr4
./bild.py tests

This script must be run from the main stringtemplate4 root directory.

Note that some of the tests fail because they are run in parallel and
they often use the same file name. I'm trying to get 4.0.8 to maven central
and don't want to change the source code during the push so I will leave it
as it is for now.
"""

# bootstrap by downloading bilder.py if not found
import urllib
import os

if not os.path.exists("bilder.py"):
    print "bootstrapping; downloading bilder.py"
    urllib.urlretrieve(
        "https://raw.githubusercontent.com/parrt/bild/master/src/python/bilder.py",
        "bilder.py")

# assumes bilder.py is in current directory
from bilder import *

VERSION = "4.0.8"

def parsers():
    download("http://www.antlr3.org/download/antlr-3.5.2-complete.jar", JARCACHE)
    antlr3("src/org/stringtemplate/v4/compiler", "gen3", version="3.5.2",
		   args=["-lib","src/org/stringtemplate/v4/compiler"],
		   package="org.stringtemplate.v4.compiler")

def compile():
    require(parsers)
    cp = uniformpath("out") + os.pathsep + \
         os.path.join(JARCACHE, "antlr-3.5.2-complete.jar") + os.pathsep
    srcpath = ["gen3", "src"]
    args = ["-Xlint", "-Xlint:-serial", "-g", "-sourcepath", string.join(srcpath, os.pathsep)]
    for sp in srcpath:
        javac(sp, "out", version="1.6", cp=cp, args=args)


def _mkjar():
    require(compile)
    # Prefix of Bundle- is OSGi cruft; it's not everything so we wrap with make_osgi_ready()
    # Declan Cox describes osgi ready jar https://github.com/antlr/antlr4/pull/689.
    manifest = \
        "Implementation-Vendor: ANTLR\n" +\
        "Implementation-Vendor-Id: org.antlr\n" +\
        "Implementation-Title: StringTemplate 4 library\n" +\
        "Implementation-Version: %s\n" +\
        "Built-By: %s\n" +\
        "Build-Jdk: %s\n" +\
        "Created-By: http://www.bildtool.org\n" +\
        "Bundle-Description: The ANTLR 4 Runtime\n" +\
        "Bundle-DocURL: http://www.antlr.org\n" +\
        "Bundle-License: http://www.antlr.org/license.html\n" +\
        "Bundle-Name: StringTemplate 4 library\n" +\
        "Bundle-SymbolicName: org.antlr.st4-osgi\n" +\
        "Bundle-Vendor: ANTLR\n" +\
        "Bundle-Version: %s\n" +\
        "\n"
    manifest = manifest % (VERSION, os.getlogin(), get_java_version(), VERSION)
    jarfile = "dist/ST4-" + VERSION + ".jar" # use mvn convention of ST4 artifact name
    jar(jarfile, srcdir="out", manifest=manifest)
    print "Generated " + jarfile
    osgijarfile = "dist/ST4-" + VERSION + "-osgi.jar"
    make_osgi_ready(jarfile, osgijarfile)
    os.rename(osgijarfile, jarfile) # copy back onto old jar
    print_and_log("Made jar OSGi-ready " + jarfile)


def mkjar(): # if called as root target
    rmdir("out")
    _mkjar()


def tests():
    cp = uniformpath("dist/ST4-" + VERSION + ".jar") \
         + os.pathsep + uniformpath("out/test") \
         + os.pathsep + os.path.join(JARCACHE, "antlr-3.5.2-complete.jar")
    args = ["-nowarn", "-Xlint", "-Xlint:-serial", "-g"]
    junit_jar, hamcrest_jar = load_junitjars()
    srcdir = uniformpath("test")
    dstdir = uniformpath("out/test")
    thiscp = dstdir + os.pathsep + cp
    thisjarwithjunit = thiscp + os.pathsep + hamcrest_jar + os.pathsep + junit_jar
    javac(srcdir, trgdir="out/test", version="1.6", cp=thisjarwithjunit, args=args)
    try:
        junit("out/test", cp=thiscp, verbose=False)
        print "tests complete"
    except:
        print "tests failed"


def install(): # mvn installed locally in ~/.m2, java jar to /usr/local/lib if present
    require(_mkjar)
    require(mksrc)
    require(mkdoc)
    jarfile = "dist/ST4-" + VERSION + ".jar"
    print_and_log("Maven installing "+jarfile+" and *-sources.jar, *-javadoc.jar")
    mvn_install(binjar=jarfile,
                srcjar="dist/ST4-" + VERSION + "-sources.jar",
                docjar="dist/ST4-" + VERSION + "-javadoc.jar",
                groupid="org.antlr",
                artifactid="ST4",
                version=VERSION)
    if os.path.exists("/usr/local/lib"):
        libjar = "/usr/local/lib/ST-" + VERSION + ".jar"
        print_and_log("Installing "+libjar)
        shutil.copyfile(jarfile, libjar)


def mksrc():
    require(parsers)
    copytree(src="gen3", trg="out/src")  # messages, Java code gen, etc...
    copytree(src="src", trg="out/src")  # messages, Java code gen, etc...
    files = allfiles("out/src", ".DS_Store")
    for f in files: rmfile(f)

    srcpath = ["out/src/org"]
    jarfile = "dist/ST4-" + VERSION + "-sources.jar"
    zip(jarfile, srcdirs=srcpath)
    print_and_log("Generated " + jarfile)


def mkdoc():
    require(mksrc)
    doc = "dist/ST4-" + VERSION + "-javadoc.jar"
    runtime_source_jarfile = "dist/ST4-" + VERSION + "-sources.jar"
    if not isstale(src=runtime_source_jarfile, trg=doc):
        return
    # JavaDoc needs antlr runtime 3.5.2 source code
    mkdir("out/Antlr352Runtime")
    download("http://search.maven.org/remotecontent?filepath=org/antlr/antlr-runtime/3.5.2/antlr-runtime-3.5.2-sources.jar", "out/Antlr352Runtime")
    unjar("out/Antlr352Runtime/antlr-runtime-3.5.2-sources.jar", trgdir="out/Antlr352Runtime")
    # go!
    mkdir("doc/ST4")
    dirs = ["src", "gen3"]
    exclude = ["org/antlr/runtime"]
    javadoc(srcdir=dirs, trgdir="doc/ST4", packages="org.stringtemplate.v4", exclude=exclude)
    zip(doc, "doc/ST4")


def clean():
    rmdir("dist")
    rmdir("out")
    rmdir("gen3")
    rmdir("doc")


def all():
    clean(True)
    _mkjar()
    tests()
    mkdoc()
    mksrc()
    install()
    clean()


processargs(globals())  # E.g., "python bild.py all"
