# StringTemplate cheat sheet

## Expression elements

See [Template expressions](templates.md#expressions)

|Syntax|Description|
|------|-----------|
|`<attribute>`|Evaluates to the string value of attribute (toString(), ToString(), \_str\_()) if it exists else empty string.|
|`<i>, <i0>`|The iteration number indexed from one and from zero, respectively, when referenced within a template being applied to an attribute or attributes. Is only visible to the template being applied to the iterator values.|
|`<attribute.property>`|Looks for property of attribute as a property (C#), then accessor methods like getProperty() or isProperty() or hasProperty(). If that fails, StringTemplate looks for a raw field of the attribute called property. Evaluates to the empty string if no such property is found.|
|`<attribute.(expr)>`|Indirect property lookup. Same as attribute.property except use the value of expr as the property name. Evaluates to the empty string if no such property is found.|
|`<multi-valued-attribute>`|Concatenation of string values of the elements. If multi-valued-attribute is missing, it evaluates to the empty string.|
|`<multi-valued-attribute; separator=expr>`|Concatenation element string values separated by expr.|
|`<[mine, yours]>`|Creates a new multi-valued attribute (a list) with elements of mine first then all of yours.|
|`<template(argument-list)>`|Include template. The argument-list is a list of attribute expressions or attribute assignments where each assignment is of the form arg-of-template=expr. expr is evaluated in the context of the surrounding template not of the invoked template. Example, bold(name) or bold(item=name) of item is an argument of template bold. The sole argument or the final argument, if argument assignments syntax is used, can be the "pass through" argument `...`|
|`<(expr)(argument-list)>`|Include template whose name is computed via expr. The argument-list is a list of attribute expressions or attribute assignments where each assignment is of the form attribute=expr. Example `<(whichFormat)()>` looks up whichFormat's value and uses that as template name. Can also apply an indirect template to an attribute.|
|`<attribute:template(argument-list)>`|Apply template to attribute with optional argument-list.  Example: `<name:bold()>` applies bold() to name's value. The first argument of the template gets the iterated value. The template is not applied to null values.|
|`<attribute:(expr)(argument-list)>`|Apply a template, whose name is computed from expr, to each value of attribute. Example `<data:(name)()>` looks up name's value and uses that as template name to apply to data.|
|`<attribute:t1(argument-list): ... :tN(argument-list)>`|Apply multiple templates in order from left to right. The result of a template application upon a multi-valued attribute is another multi-valued attribute. The overall expression evaluates to the concatenation of all elements of the final multi-valued attribute resulting from templateN's application.|
|`<attribute:{x \| anonymous-template}>`|Apply an anonymous template to each element of attribute.  The iterated value is set to argument x. The anonymous template references ﻿`<x>` to access the iterator value.|
|`<a1,a2,...,aN:{argument-list \| anonymous-template}>`|Parallel list iteration. March through the values of the attributes a1..aN, setting the values to the arguments in argument-list in the same order. Apply the anonymous template.|
|`<attribute:t1(),t2(),`...`,tN()>`|Apply an alternating list of templates to the elements of attribute. The template names may include argument lists.|
|`\<` or `\>`|escaped delimiter prevents `<` or `>` from starting an attribute expression and results in that single character.|
|`<\ >, <\n>, <\t>, <\r>`|special character(s): space, newline, tab, carriage return. Can have multiple in single `<...>` expression.|
|`<\uXXXX>`|Unicode character(s). Can have multiple in single `<...>` expression.|
|`<\\>`|Ignore the immediately following newline char. Allows you to put a newline in the template to better format it without actually inserting a newline into the output|
|`<! comment !>`|Comments, ignored by StringTemplate.|

<a name="functions"></a>
## Functions

|Syntax|Description|
|------|-----------|
|`<first(attr)>`|The first or only element of attr. You can combine operations to say things like first(rest(names)) to get second element.|
|`<length(attr)>`|Return the length of a mult-valued attribute or 1 if it is single attribute. If attribute is null return 0. Strings are not special; i.e., length("foo") is 1 meaning "1 attribute". Nulls are counted in lists so a list of 300 nulls is length 300. If you don't want to count nulls, use length(strip(list)).|
|`<strlen(attr)>`|Return the length of a string attribute; runtime error if not string.|
|`<last(attr)>`|The last or only element of attr.|
|`<rest(attr)>`|All but the first element of attr. Returns nothing if <attr> is single valued.|
|`<reverse(attr)>`|Return a list with the same elements as v but in reverse order. null values are NOT stripped out. Use reverse(strip(v)) to do that.|
|`<trunc(attr)>`|Returns all elements but last element.|
|`<strip(attr)>`|Return a new list w/o null values.|
|`<trim(attr)>`|Trim whitespace from back/front of a string; runtime error if not string.|

## Statements

See [Templates conditionals](templates.md#conditionals)

If `attribute` has a value or is a boolean object that evaluates to true, include `subtemplate `else include `subtemplate2`. These conditionals may be nested.
```
<if(attribute)>subtemplate 
<else>subtemplate2 
<endif>
```

First attribute that has a value or is a boolean object that evaluates to true, include that subtemplate. These conditionals may be nested.

```
<if(x)>subtemplate 
<elseif(y)>subtemplate2 
<elseif(z)>subtemplate3 
<else>subtemplate4 
<endif>
```

If attribute has no value or is a bool object that evaluates to false, include subtemplate. These conditionals may be nested.

```
<if(!attribute)>subtemplate<endif>
```

Conditional expressions can include "or" and "and" operations as well as parentheses. E.g.,

```
<if((!a||b)&&!(c||d))>broken<else>works<endif>
```

## Groups

See [Group file syntax](groups.md)

```
t1(arg1,arg2,...,argN) ::= "template1" // single-line template
// multi line template
t2(args) ::= << 
template2
>>
// multi line template that ignores indentation and newlines
t2(args) ::= <%
template3
%>
```

To import other templates, use the import statement:

```
import "directory"
import "template file.st"
import "group file.stg"
```

The paths can be absolute, but should probably be relative to the class path or the directory of the template that imports them.

## Reserved words

Don't use these context-sensitive identifiers as attribute names or template names:

*true, false, import, default, key, group, implements, first, last, rest, trunc, strip, trim, length, strlen, reverse, if, else, elseif, endif, delimiters*.
