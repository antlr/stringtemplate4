# Introduction

First, to learn more about StringTemplate's philosophy, you can check out the very readable academic paper [Enforcing Strict Model-View Separation in Template Engines](http://www.cs.usfca.edu/~parrt/papers/mvc.templates.pdf) (*nominated for best paper at WWW2004*).

Most programs that emit source code or other text output are unstructured blobs of generation logic interspersed with print statements. The primary reason is the lack of suitable tools and formalisms. The proper formalism is that of an output grammar because you are not generating random characters--you are generating sentences in an output language. This is analogous to using a grammar to describe the structure of input sentences. Rather than building a parser by hand, most programmers will use a parser generator. Similarly, we need some form of *unparser generator* to generate text. The most convenient manifestation of the output grammar is a template engine such as StringTemplate.

A template engine is simply a code generator that emits text using templates, which are really just documents with "holes" in them where you can stick values called attributes. An attribute is either a program object such as a string or `VarSymbol` object, a template instance, or a sequence of attributes including other sequences. Template engines are domain-specific languages for generating structured text. StringTemplate breaks up your template into chunks of text and attribute expressions, which are by default enclosed in angle brackets <attribute-expression> (but you can use whatever single character start and stop delimiters you want). StringTemplate ignores everything outside of attribute expressions, treating it as just text to spit out. To evaluate a template and generate text, we "render" it with a method call:

```java
ST.render()
```

For example, the following template has two chunks, a literal and a reference to attribute `name`:

```
Hello, <name>
```

Using templates in code is very easy. Here is the requisite example that prints `Hello, World`:
 
```java
import org.stringtemplate.v4.*;
...
ST hello = new ST("Hello, <name>");
hello.add("name", "World");
System.out.println(hello.render());
``` 
 
|MVC Pattern|
|-----------|
|In the parlance of the model-view-controller (MVC) pattern, templates represent the view and the code fragment represents both model (the name string) and controller (that pulls from the model and injects attributes into the view).|

StringTemplate is not a "system" or "engine" or "server". It is designed to be embedded inside other applications and is distributed as a small library with no external dependencies except ANTLR (used for parsing the StringTemplate template language).

## Groups of templates

The primary classes of interest are `ST`, `STGroupDir`, and `STGroupFile`. You can directly create a template in code, you can load templates from a directory, and you can load a file containing a collection of templates (a template group file). Group files behave like zips or jars of template directories.

For example, let's assume we have two templates in files `decl.st` and `init.st` in directory `/tmp`:

```
// file /tmp/decl.st
decl(type, name, value) ::= "<type> <name><init(value)>;"
```

```
// file /tmp/init.st
init(v) ::= "<if(v)> = <v><endif>"
```

We can access those templates by creating a `STGroupDir` object. We then ask for an instance with `getInstanceOf()` and inject attributes with `add()`:
 
```java
STGroup group = new STGroupDir("/tmp");
ST st = group.getInstanceOf("decl");
st.add("type", "int");
st.add("name", "x");
st.add("value", 0);
String result = st.render(); // yields "int x = 0;"
```

If you would like to keep just the template text and not the formal template definition around the template text, you can use `STRawGroupDir`. Then, `decl.st` would hold just the following:

```
// file /tmp/decl.st
<type> <name><init(value)>;
```

That makes it easier for graphics designers and HTML people to work with template files, although the formal parameter definitions are okay for people using these to generate source code as they will usually be programmers not graphics people.

This example demonstrates some key syntax and features. Template definitions look very similar to function definitions except that the bodies are strings. Template `decl` takes three arguments by reference but uses only two of them directly. Instead of expanding `value` immediately, it invokes/includes template `init` with `value` as the argument. Alternatively, template `init` could be given no arguments, and it would still see attribute `value` because of *dynamic scoping*. That essentially means that a template can reference the attributes of any invoking template.

Note, that to get the spacing correct, there is no space between expression `<name>` and `<init()>`. If we do not inject a declaration initialization (attribute value), we don't want to space between the name and the `;`. Template `init` emits ` = <v>` only if the controller code injects a value, which we do here (0). In this case, we have injected two strings and one integer, but we can send in any object we want; more on that below.

Sometimes it's more convenient to collect templates together into a single unit called the group file. For example, we can collect the definitions in the separate template `.st` files into an equivalent `.stg` group file:

```
// file /tmp/test.stg
decl(type, name, value) ::= "<type> <name><init(value)>;"
init(v) ::= "<if(v)> = <v><endif>"
```

To pull templates from this file instead of a directory, all we have to do is change our constructor to use `STGroupFile`:
 
```java
STGroup group = new STGroupFile("/tmp/test.stg");
ST st = group.getInstanceOf("decl");
st.add("type", "int");
st.add("name", "x");
st.add("value", 0);
String result = st.render(); // yields "int x = 0;"
```

## Accessing properties of model objects

Template expressions can access the properties of objects injected from the model. For example, consider the following `User` object.

```java
public static class User {
    public int id; // template can directly access via u.id
    private String name; // template can't access this
    public User(int id, String name) { this.id = id; this.name = name; }
    public boolean isManager() { return true; } // u.manager
    public boolean hasParkingSpot() { return true; } // u.parkingSpot
    public String getName() { return name; } // u.name
    public String toString() { return id+":"+name; } // u
}
```
 
We can inject instances of `User` just like predefined objects like strings and can refer to properties using the `.` dot property access operator. StringTemplate interprets `o.p` by looking for property `p` within object `o`. The lookup rules differ slightly between language ports, but in general they follow the old JavaBeans naming convention. StringTemplate looks for methods `getP()`, `isP()`, `hasP()` first. If it fails to find one of those methods, it looks for a field called `p`. In the following example, we access properties `id` and `name`. Also note that the template uses `$...$` delimiters, which makes more sense since we are generating HTML.
 
```java
ST st = new ST("<b>$u.id$</b>: $u.name$", '$', '$');
st.add("u", new User(999, "parrt"));
String result = st.render(); // "<b>999</b>: parrt"
```
 
Property reference `u.id` evaluates to the field of the injected User object whereas `u.name` evaluates to the "getter" for the field name.

StringTemplate renders all injected attributes and any reference properties to text using the `render()` method. In this case, a reference to `$u$` would yield `999:parrt`.

### Injecting data aggregate attributes

Being able to pass in objects and access their fields is very convenient but often we don't have a handy object to inject. Creating one-off data aggregates is a pain, you have to define a new class just to associate two pieces of data. StringTemplate makes it easy to group data during `add()` calls. You may pass in an aggregrate attribute name to `add()` with the data to aggregate. The syntax of the attribute name describes the properties. For example `a.{p1,p2,p3}` describes an attribute called a that has three properties `p1`, `p2`, `p3`. Here's an example:

 
```java
ST st = new ST("<items:{it|<it.id>: <it.lastName>, <it.firstName>\n}>");
st.addAggr("items.{ firstName ,lastName, id }", "Ter", "Parr", 99); // add() uses varargs
st.addAggr("items.{firstName, lastName ,id}", "Tom", "Burns", 34);
String expecting =
        "99: Parr, Ter\n"+
        "34: Burns, Tom\n"+
```
 
### Applying templates to attributes

Let's look more closely at how StringTemplate renders attributes. It does not distinguish between single and multi-valued attributes. For example, if we add attribute `name` with value `"parrt"` to template `<name>`, it renders to `parrt`. If we call `add()` twice, adding values `"parrt"` and `"tombu"` to `name`, it renders to `parrttombu`. In other words, multi-valued attributes render to the concatenation of the string values of the elements. To insert a separator, we can use the `separator` option: `<name; separator=", ">`. Without changing the attributes we inject, the output for that template is `parrt, tombu`. To alter the output emitted for each element, we need to iterate across them.

StringTemplate has no *foreach* statement. Instead, we apply templates to attributes. For example, to surround each name with square brackets, we can define a bracket template and apply it to the names:

```
test(name) ::= "<name:bracket()>" // apply bracket template to each name
bracket(x) ::= "[<x>]"            // surround parameter with square brackets
```

Injecting our list of names as attribute name into template test yields `[parrt][tombu]`. StringTemplate sets the first parameter of the template it's applying (`x` in this case) to the iterated value.

Combining with the separator operator yields `[parrt], [tombu]`:

```
test(name) ::= "<name:bracket(); separator=\", \">"
bracket(x) ::= "[<x>]"
```

StringTemplate is dynamically typed in the sense that it doesn't care about the types of the elements except when we access properties. For example, we could pass in a list of `User` objects, `User(999,"parrt")` and `User(1000,"tombu")`, and the templates would work without alteration. StringTemplate would use the "to string" evaluation function appropriate for the implementation language to evaluate `<x>`. The output we'd get is `[999:parrt], [1000:tombu]`.

Sometimes creating a separate template definition is too much effort for a one-off template or a really small one. In those cases, we can use anonymous templates (or subtemplates). Anonymous templates are templates without a name enclosed in curly braces. They can have arguments, though, just like a regular template. For example, we can redo the above example as follows.

```
test(name) ::= "<name:{x | [<x>]}; separator=\", \">"
```

Anonymous template `{x | [<x>]}` is the in-lined version of `bracket()`. Argument names are separated from the template with the `|` pipe operator.

StringTemplate will iterate across any object that it can reasonably interpret as a collection of elements such as arrays, lists, dictionaries and, in statically typed ports, objects satisfying iterable or enumeration interfaces.

### General use of StringTemplate for formatting

The `ST.format()` method is great for general use in general code.

```java
int[] num =
    new int[] {3,9,20,2,1,4,6,32,5,6,77,888,2,1,6,32,5,6,77,
        4,9,20,2,1,4,63,9,20,2,1,4,6,32,5,6,77,6,32,5,6,77,
        3,9,20,2,1,4,6,32,5,6,77,888,1,6,32,5};
String t =
    ST.format(30, "int <%1>[] = { <%2; wrap, anchor, separator=\", \"> };", "a", num);
System.out.println(t);
```
 
yields:

```
int a[] = { 3, 9, 20, 2, 1, 4,
            6, 32, 5, 6, 77, 888,
            2, 1, 6, 32, 5, 6,
            77, 4, 9, 20, 2, 1,
            4, 63, 9, 20, 2, 1,
            4, 6, 32, 5, 6, 77,
            6, 32, 5, 6, 77, 3,
            9, 20, 2, 1, 4, 6,
            32, 5, 6, 77, 888,
            1, 6, 32, 5 };
```
