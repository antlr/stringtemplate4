#!/usr/bin/env python
import os

"""
This script uses my experimental build tool http://www.bildtool.org

This script deploys artifacts created by bild.py.

Windows build using this script is not yet supported.

cd /usr/local/antlr/stringtemplate4
./deploy.py mvn_snapshot

or

./deploy.py mvn_deploy

or


./deploy.py pypi

or

./deploy.py # does "all"

This script must be run from the main antlr4 directory.
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

def mvn_snapshot():  # assumes that you have ~/.m2/settings.xml set up
    binjar = uniformpath("dist/ST4-%s.jar" % VERSION)
    docjar = uniformpath("dist/ST4-%s-javadoc.jar" % VERSION)
    srcjar = uniformpath("dist/ST4-%s-sources.jar" % VERSION)
    mvn(command="deploy:deploy-file",
        binjar=binjar,
        srcjar=srcjar,
        docjar=docjar,
        repositoryid="ossrh",
        artifactid="ST4",
        pomfile="pom.xml",
        url="https://oss.sonatype.org/content/repositories/snapshots")


# deploy to maven central
def mvn_deploy():  # assumes that you have ~/.m2/settings.xml set up
    binjar = uniformpath("dist/ST4-%s.jar" % VERSION)
    docjar = uniformpath("dist/ST4-%s-javadoc.jar" % VERSION)
    srcjar = uniformpath("dist/ST4-%s-sources.jar" % VERSION)
    mvn(command="gpg:sign-and-deploy-file",
        binjar=binjar,
        srcjar=srcjar,
        docjar=docjar, repositoryid="ossrh",
        pomfile="pom.xml",
        url="https://oss.sonatype.org/service/local/staging/deploy/maven2/")


def website():
    """
    Push all jars, source, target artifacts etc.
    """
    # There is no JavaScript project i.e; nothing to "build" it's just a bunch of files that I zip.
    pass


def all():  # Note: building artifacts is in a separate file bild.py
    mvn()
    website()


processargs(globals())  # E.g., "python bild.py all"
