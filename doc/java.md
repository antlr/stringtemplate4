# Using StringTemplate with Java

## Installation

All you need to do is get the StringTemplate jar into your CLASSPATH as well as its dependent ANTLR jar. [Download Java StringTemplate 4.0.8 binary jar](http://www.stringtemplate.org/download.html) and put into your favorite lib directory such as `/usr/local/lib` on UNIX. Add to your CLASSPATH. On UNIX that looks like
 
```bash
$ export CLASSPATH="/usr/local/lib/ST-4.0.8.jar:$CLASSPATH"
```
 
Java will now see all the libraries necessary to execute ST stuff. Also, check out the [StringTemplate repo](https://github.com/antlr/stringtemplate4).

## Hello world

Here's a simple, complete program to test your installation.

```java
import org.stringtemplate.v4.*;
 
public class Hello {
    public static void main(String[] args) {
        ST hello = new ST("Hello, <name>");
        hello.add("name", "World");
        System.out.println(hello.render());
    }
}
```

Here's how to compile and run it from the command line:

```bash
/tmp $ javac Hello.java
/tmp $ java Hello
Hello, World
```

## Loading template groups

### Group files

To load a group file, use the STGroupFile subclass of STGroup:

```java
//load file name
STGroup g = new STGroupFile("test.stg");
```

This tells StringTemplate to look in the current directory for test.stg. If not found, STGroupFile looks in the CLASSPATH. You can also use a relative path. The following looks for subdirectory templates in the current directory or, if not found, in a directory of the CLASSPATH.

```java
// load relative file name
STGroup g = new STGroupFile("templates/test.stg");
```

You can also use a fully qualified name:

```
// load fully qualified file name
STGroup g = new STGroupFile("/usr/local/share/templates/test.stg");
```

### Group directories

Group files, described above, are like directories of templates packed together into a single file (like text-based jars). To load templates stored within a directory as separate .st files, use STGroupDir instances:

```java
// load relative directory of templates
STGroup g = new STGroupDir("templates");
```

If templates is not found in the current directory, StringTemplate looks in the CLASSPATH. Or, you can specify the exact fully qualified name:

```
// load fully qualified directory of templates
STGroup g = new STGroupDir("/usr/local/share/templates");
```

### Group strings

For small groups, it sometimes makes sense to use a string within Java code:

```java
String g =
    "a(x) ::= <<foo>>\n"+
    "b() ::= <<bar>>\n";
STGroup group = new STGroupString(g);
ST st = group.getInstanceOf("a");
String expected = "foo";
String result = st.render();
assertEquals(expected, result);
```

### URL/URI/Path quagmire

Make sure to pass either a valid file name as a string or a valid URL object. File/dir names are relative like `foo.stg`, `foo`, `org/foo/templates/main.stg`, or `org/foo/templates` OR they are absolute like `/tmp/foo`. This is incorrect:

```
// BAD
STGroup modelSTG = new STGroupFile(url.getPath());
```

because it yields a file path to a jar and then inside:

```
file:/somedirectory/AJARFILE.jar!/foo/main.stg
```

This isn't a valid file system identifier. To use URL stuff, pass in a URL object not a string. See [Converting between URLs and Filesystem Paths](http://maven.apache.org/plugin-developers/common-bugs.html#Converting_between_URLs_and_Filesystem_Paths) for more information.

## API documentation

[Java API](http://www.stringtemplate.org/api/index.html)
