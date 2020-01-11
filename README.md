ST (StringTemplate) is a java template engine (with ports for C#, Python, and Objective-C coming) for generating source code, web pages, emails, or any other formatted text output. ST is particularly good at multi-targeted code generators, multiple site skins, and internationalization / localization.  It evolved over years of effort developing jGuru.com and then ANTLR v3.

*Given day-job constraints, my time working on this project is limited so I'll have to focus first on fixing bugs rather than changing/improving the feature set. Likely I'll do it in bursts every few months. Please do not be offended if your bug or pull request does not yield a response! --parrt*

The main website is:

> http://www.stringtemplate.org

Its distinguishing characteristic is that it strictly enforces
model-view separation, unlike other engines. See:

> http://www.cs.usfca.edu/~parrt/papers/mvc.templates.pdf

The documentation is in this repo

> https://github.com/antlr/stringtemplate4/tree/master/doc/index.md

Per the BSD license in [LICENSE.txt](LICENSE.txt), this software is not
guaranteed to work and might even destroy all life on this planet.

## INSTALLATION

### Manual Installation

All you need to do is get the StringTemplate jar into your `CLASSPATH`. See [Java StringTemplate](doc/java.md).

### Maven

To reference StringTemplate from a project built using Maven, add the following
to the `<dependencies>` element in your **pom.xml** file.

```xml
<dependency>
  <groupId>org.antlr</groupId>
  <artifactId>ST4</artifactId>
  <version>4.2</version>
  <scope>compile</scope>
</dependency>
```

### Gradle

In `build.gradle`, add the following dependency:

```groovy
dependecies {
    // ...

    // https://mvnrepository.com/artifact/org.antlr/ST4
    compile group: 'org.antlr', name: 'ST4', version: '4.2'
}
```

Make sure you are using the `mavenCentral` repository by adding it if necessary:

```groovy
repositories {
    // ...
    mavenCentral()
}
```

### Other

Select a version on [mvnrepository](https://mvnrepository.com/artifact/org.antlr/ST4),
and copy the snippet relevant to your build tool.

## BUILDING FROM SOURCE

The source is at github.com:

> https://github.com/antlr/stringtemplate4

If you would like to make changes to ST and build it yourself,
just run `mvn compile` from the root directory of the repo.

You can also run `ant` from the root dir.
