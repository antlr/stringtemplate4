# Group file syntax

Group files are collections of templates and dictionaries and have `.stg` file suffixes. Group files can also import templates or groups. The basic format looks like:

```
delimiters 
imports 
dictionaries 
templates
```

## Setting template expression delimiters

Use delimiters keyword to set delimiters per group file:

```
delimiters "%", "%"
t(x) ::= "%x%"
<...> are the default.
```

The legal delimiters are pretty limited. Avoid using characters that can be part of an expression like `(...)` and `[...]`.

## Import statements

You can import a single template file, a group file, or a directory of templates. File suffixes within the string operand indicates which:

```
// /tmp/main.stg
import "/tmp/test.st"   // import a single template
import "/tmp/test.stg"  // import a group of templates from a file
import "/tmp/test"      // import a directory of templates
```

Instead of providing fully qualified path names, it's more flexible to specify relative path names. For example, if we import just the file name, StringTemplate looks for the files in the directory of the referring file (/tmp, in this case):

```
// /tmp/main.stg
import "test.st"   // import a single template
import "test.stg"  // import a group of templates from a file
import "test"      // import a directory of templates
```

**In the Java-reference implementation**, StringTemplate also looks for files and directories in the CLASSPATH. Using relative paths is particularly important if we want to load templates from jar files. Java cannot find files specified with absolute path names within jar files. Please refer to [Using StringTemplate with Java](java.md).

Templates with the same name override templates from imported groups just like method overriding and class inheritance. See [Group inheritance](inheritance.md).

## Dictionaries

There are situations where you need to translate a string in one language to a string in another language. For example, you might want to translate integer to int when translating Pascal to C. You could pass a Map or IDictionary (e.g. hashtable) from the model into the templates, but then you have output literals in your model:-1:. The StringTemplate solution is to support a dictionary feature. For example, here is a dictionary that maps Java type names to their default initialization values:

```
typeInitMap ::= [
        "int":"0",
        "long":"0",
        "float":"0.0",
        "double":"0.0",
        "boolean":"false",
        "byte":"0",
        "short":"0",
        "char":"0",
        default:"null" // anything other than an atomic type
]
```

To use the dictionary in a template, refer to it as you would an attribute. `<typeInitMap.int>` returns `0` from the map. If your type name is an attribute not a constant like `int`, then use an indirect property access: `<typeInitMap.(typeName)>`.

Dictionary strings can also be templates that can refer to attributes that will become visible via dynamic scoping of attributes once the dictionary value has been embedded within a template.

Large strings, such as those with newlines, can be specified with the usual large template delimiters from the group file format: `<<...>>`.

The default and other mappings cannot have empty values. They have empty values by default. If no key is matched by the map then an empty value is returned. The keyword key is available if you would like to refer to the key that maps to this value. This is particularly useful if you would like to filter certain words but otherwise leave a value unchanged; use `default : key` to return the key unmolested if it is not found in the map.

Dictionaries are defined in the group's scope and are visible if no attribute hides them. For example, if you define a formal argument called typeInitMap in template foo then foo cannot see the map defined in the group (though you could pass it in as another parameter). If a name is not an attribute and it's not in the group's maps table, then any imported groups are consulted. You may not redefine a dictionary and it may not have the same name as a template in that group. The default clause must be at the end of the map.

You'll notice that square brackets denote data structure in other areas too such as `[a,b,c,...]` which makes a singe multi-valued attribute out of other attributes so you can iterate across them.

## Template definitions

Template definitions look like function definitions with untyped arguments:

```
templateName(arg1, arg2, ..., argN) ::= "single-line template"
```

or

```
templateName(arg1, arg2, ..., argN) ::= <<
multi-line template
>>
```

or

```
templateName(arg1, arg2, ..., argN) ::= <%
multi-line template that ignores indentation and newlines
%>
```

## Default values

You can give default values to arguments as well (to the consecutive final n arguments). For example, here is a template that defines classes to extend Object if the invoking template does not set or the controller code does not inject attribute sup:

```
class(name,members,sup="Object") ::= "class <name> extends <sup> { <members> }"
```

## Region definitions

Regions are small chunks of template code extracted from a larger surrounding template. To define or override a region, prefix the template definition with the surrounding template name:

```
@surroundingTemplate.templateName(arg1, arg2, ..., argN) ::= "single-line template"
```

See [Template regions](regions.md) for more details.

## Aliases

To alias a template to another, use notation

```
aliasName ::= templateName
```

This is useful when controller code refers to two different templates, but we want to implement both in the same way without cutting and pasting.

## Formal grammar

See [StringTemplate 4 group file grammar](https://github.com/antlr/grammars-v4/blob/master/stringtemplate/STGParser.g4).
