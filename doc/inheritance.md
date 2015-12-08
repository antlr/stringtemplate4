# Group inheritance

It's a common problem to need to generate a website with multiple skins or a code generator that must generate multiple variations of the same language such as Java 1.4 and 1.5 (1.5 added enums, for example). In either case, we want to design a variation as it differs from an existing code generation target. The easiest way to do this is to create a group of templates that override templates from a "supergroup" using the import statement. Templates in the current group override templates inherited from the imported group.
Let's look at the problem of generating enumerated types. In Java 1.4, we simulate enums with something like this:

```java
// Java 1.4
public static final int MyEnum_A = 1;
public static final int MyEnum_B = 2;
```

whereas Java 1.5 lets us do this:

```java
// Java 1.5
public enum MyEnum { A, B }
```

The goal is to keep the model and controller the same and to use a different group of templates according to the output we need. Here is what a piece of a Java 1.4 code generation template group file:

```
// Java1_4.stg
class(name, members) ::= <<
class <name> {
    <members>
}
>>
 
constants(typename, names) ::= "<names:{n | <constant(n,i)>}; separator={<\n>}>"
 
constant(n) ::= "public static final int <typename>_<n>=<i>;"
```

Instead of copying and altering the entire group for Java 1.5, we can import the 1.4 group and alter just the part that changes, template `constants`:

```
// Java1_5.stg
import "Java1_4.stg"
 
/** Override constants from Java1_4.stg */
constants(typename, names) ::= <<
public enum <typename> { <names; separator=", "> }
>>
```

Group Java1\_5 the inherited template classs will write template constants. The following sample code creates group objects referring to both template group files, creates and filled in instances of template class, and finally prints out the rendered text.

```java
public void test(String[] args) {
    STGroup java1_4 = new STGroupFile("/tmp/Java1_4.stg");
    STGroup java1_5 = new STGroupFile("/tmp/Java1_5.stg");
    System.out.println( getCode(java1_4) );
    System.out.println( getCode(java1_5) );
}
public String getCode(STGroup java) {
    ST cl = java.getInstanceOf("class"); // create class
    cl.add("name", "T");
    ST consts = java.getInstanceOf("constants");
    consts.add("typename", "MyEnum");
    consts.add("names", new String[] {"A","B"});
    cl.add("members", consts); // add constants as a member
    return cl.render();
}
```
 
Here is the output we get:

```java
// StringTemplate output
class T {
    public static final MyEnum_A=1;
    public static final MyEnum_B=2;
}
class T {
    public enum MyEnum { A, B }
}
```

Simply by switching template group pointers in the test function, we have generated different code--only the templates changed.

## Template polymorphism

In object-oriented programming languages, the type of the receiving object dictates which overridden method to call and response to method call `o.f()`. Similarly, StringTemplate looks up templates according to the group of the template doing the invocation. Template *polymorphism* is at work.

Imagine we're generating an HTML page using templates from group file site.stg (using `$...$` delimiters) that invokes a searchbox template defined within the same group file:

```
// site.stg
page(content) ::= <<
<html>
<body>
$searchbox()$
$content$
</body>
</html>
>>
 
searchbox() ::= "<form method=get action=/search>...</form>"
```

To create an instance of page, inject some test content, and print it out, we can use the following controller code.

```java
STGroup g = new STGroupFile("site.stg", '$', '$');
ST page = g.getInstanceOf("page");
page.add("content", "a test page");
System.out.println(page.render());
```
 
We get the following output.

```html
<!-- StringTemplate output -->
<html>
<body>
<form method=get action=/search>...</form>
a test page
</body>
</html>
```

Now, let's define a subgroup that overrides searchbox so that it generates nothing.

```
// bland.stg
import "site.stg"
searchbox() ::= ""
```

We can use identical code except for changing the source of the templates:

```java
STGroup g = new STGroupFile("bland.stg", '$', '$');
...
```

That yields the following output

```html
<!-- StringTemplate output -->
<html>
<body>
a test page
</body>
</html>
```

Template page uses the overwritten version of the search box because we created that the page template via the subgroup. A template instantiated via the bland group should always start looking for templates in bland rather than the site supergroup even though searchbox is physically deﬁned within site.stg.

## Dynamic inheritance

When dealing with lots of output language variations, a proper separation of concerns can make generating multiple targets very complicated. For example, adding a debugging variation to our Java templates from above means adding another group of templates derived from both the 1.4 and 1.5 templates. The goal is to isolate all debugging code fragments in one group instead of cramming it all into the main templates. The problem is that we need a new debugging variation for each of the 1.4 and 1.5 templates--the import statement takes a literal string. We would need a new debugging group file to refer to different Java group file variations. In other words, DbgJava1\_4.stg would have `import "Java1_4.stg"` and DbgJava1\_5.stg would have `import "Java1_5.stg"`.

Obviously the number of variations can explode. To deal with this problem, StringTemplate allows you to dynamically import/inherit templates using the controller instead of an import statement in the group file (and is the only way to do it when using directories of templates instead of group files). Here's some code that assumes there are no imports in the group files. To create group dbg\_java1\_5, it imports java1\_5 group, which itself imports java1\_4.
 
```java
STGroup java1_4 = new STGroupFile("/tmp/Java1_4.stg");
STGroup java1_5 = new STGroupFile("/tmp/Java1_5.stg");
java1_5.imporTemplates(java1_4); // import "Java1_5.stg"
STGroup dbg_java1_4 = new STGroupFile("/tmp/Dbg.stg");
STGroup dbg_java1_5 = new STGroupFile("/tmp/Dbg.stg");
dbg_java1_4.importTemplates(java1_4); // import "Java1_4.stg"
dbg_java1_5.importTemplates(java1_5); // import "Java1_5.stg"
```
 
## Inheritance and subdirectories

There are 2 kinds of people using StringTemplate: web type folks and code generation type folks. The web folks usually have directory trees full of templates and code generation people tend to have group files. Everything is fine with respect to inheritance as long as the two don't mix. It's just too complicated thinking about inheritance hierarchies as well as directory hierarchies as well as polymorphism all at the same time. So, I've made this illegal by throwing an unsupported operation exception if you try to do an import in a group filed its nested within an outer STGroupDir.

Remember, for each group there should only be one STGroup object. So, imagine we have group file foo.stg and template a.st in a directory called /tmp and we create a group object to handle that stuff:
 
```java
STGroup dir = STGroupDir("/tmp");
dir.getInstanceOf("a"); // no problem; looks in "/tmp/a.st"
dir.getInstanceOf("/foo/b"); // no problem if foo.stg has b() template
```

So far so good. Now, what you cannot do is have foo.stg import something because it is nested within dir:

```
import "bar.stg"  // causes unsupported operation exception
b() ::= "..."
```

If I did,

```java
STGroup g = new STGroupFile("/tmp/foo.stg");
```

then there is no problem. Difference is that you don't want to mix inheritance with subdirectories and a group file within a STGroupDir acts like a subdirectory.