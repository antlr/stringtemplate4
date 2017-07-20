
# Templates

Templates are essentially exemplars of the desired output with "holes" where the programmer may stick untyped values called attributes or other template instances. To enforce model-view separation, templates may not test nor compute with attribute values and, consequently, attributes have no need for type information. Templates may, however, know the data is structured in a particular manner such as a tree structure.

A template is a sequence of text and expression elements, optionally interspersed with comments. At the coarsest level, the basic elements are:

    text
    <expr>
    <! comment !>

Escape delimiters with a backslash character: `\<` or `\>`.

| Template expression delimiters |
|------|
|This documentation uses `<...>` to delimit expressions, but you can use any single start and stop character. For HTML, `$..$` is a much better choice obviously. You can set the delimiters as you create templates or template groups.|

Attribute expressions combine canonical operations that are limited to operate on the surrounding template's attribute table or, using dynamic scoping, to operate on any enclosing template instance's attributes. All expressions are side-effect free and, thus, there are no variable assignments. Further, expressions may not affect prior computations nor the surrounding template. The four canonical attribute expression operations are:

* attribute reference<br>`<name>`
* template include<br>`<supportcode()>`
* conditional include<br>`<if(trace)>print("enter function");<endif>`
* template application (i.e., map operation)<br> `<vars:decl()>`

<a name="literals"></a>
## Expression literals

StringTemplate has the following literals.

|Syntax|Description|
|------|-----------|
|`true`| Boolean true value|
|`false`|Boolean false value|
|`char`| char → space &#124; \n &#124; \r &#124; \t &#124; \uXXXX|
|`\\`|Ignore the immediately following newline char. Allows you to put a newline in the template to better format it without actually inserting a newline into the output|
|`"`*string*`"`|A string of output characters|
|`{`*template*`}`|An anonymous subtemplate|
|`{`*args* `|` *template*`}`|An anonymous subtemplate with arguments|
|`[]`|An an empty list.|
|`[`*expr1*`,` *expr2*`,` ...`,` *exprN*`]`|A list with N values. It behaves like an array or list injected from controller code.|

For more on list semantics, see [The real story on null vs empty](null-vs-empty.md).

<a name="expresssions"></a>
## Attribute expressions

|Syntax|Description|
|------|-----------|
|*a*|Look up a and converts it to a string using the appropriate implementation language conversion function such as toString(), ToString(), o _str(). StringTemplate uses dynamic scoping and so it looks up the enclosing template chain searching for _a. If not defined locally as a formal argument, StringTemplate looks at the template that invoked the current template, and so on. Evaluates to the empty string if a does not exist.|
|`(`*expr*`)`|Evaluate expr to a string. This is useful for computing the name of the template or property.|
| *expr*`.p`|Get property p of expr. expr is typically just an attribute; e.g., in expression <user.name>, expr is user and p is name. Looks for p as a property (C#), then accessor methods getProperty() or isProperty() or hasProperty(). If that fails, StringTemplate looks for a raw field of the attribute called p. Evaluates to the empty string if no such property is found. See also Introduction#properties|
|*expr1*`.(`*expr2*`)`|Evaluate expr2 to a string and use that as the name of a property, reducing this case to the previous.|

<a name="includes"></a>
## Template include

### Syntax

*expr*`(`*args*`)`

where

*args* →<br>
*args* → `...`<br>
*args* → *expr1*`,` *expr2*`,` \_\_, *exprN*<br>
*args* → *a1*`=`*expr1*`,` *a2*`=`*expr2*`,` \_\_`,` *aN*`=`*exprN*<br>
*args* → *a1*`=`*expr1*`,` *a2*`=`*expr2*`,` \_\_`,` *aN*`=`*exprN*, `...`<br>

Templates have optional arguments. These arguments can be a list of expressions, as in most languages, or a list of assignments to the named arguments of the target template. The list of assignment style is less efficient but useful for templates that take many arguments.

Normally, formal argument definitions hide any attributes visible above in the enclosing template chain. If you replace the argument list with an ellipsis, `...`, the included template can see all of the attributes from above. If you use a named argument assignments, the last argument can also be this "pass through" operator. In this case, it sets any remaining arguments. In general, the pass through operator sets argument *x* of the included template to be whatever *x* is in the enclosing template.

### Discussion

To include another template, just reference that template like a function call with any arguments you need. Note that *expr* is either a template name or a parenthesized expression that evaluates to the name of a template; e.g., `(templateName)()`. It is an error if there is a mismatch between the number of values passed into *t* and *t*'s formal arguments.

<a name=map></a>
## Applying (mapping) templates across attributes

### Syntax

Case 1 → *expr* `: t(`*args*`)`<br>
Case 2 → *expr* `: t1(`*args*`), t2(`*args*`),` ...`, tN(`*args*`)`<br>
Case 3 → *expr1*`,` ...`,` *exprN*` : t(`*args*`)`

Templates `t1`..`tN` can be template names or anonymous subtemplates that define the appropriate number of arguments:

```
{a1, a2, ..., aN | ...}
```

### Discussion

**Case 1.** StringTemplate applies template t to each element of the expr. It assigns the array value to the first attribute in the formal argument list of t. Any extra args passed in, are assigned to the other formal arguments. (It is an error if there is a mismatch between the number of values passed into t and t's formal arguments.)

Consider a list of variable names, names, that we want to surround with parentheses and assume we have a template called parens:

```
parens(x) ::= "(<x>)"
```

Expression `names:parens()` invokes parens once for every element of names. For example,

```
["a", "b", "c"]:parens()
```

yields `(a)(b)(c)`. Formal argument `x` is assigned the iterated value at each invocation of parens. In contrast,

```
parens(["a", "b", "c"])
```

yields `(abc)`.

Note that, for single valued attributes, template application is like an alternate include syntax. For example, `"a":parens()` is the same as `parens("a")`.

**Case 2.** StringTemplate does a roundrobin walk through the templates. This arose for the case with HTML tables where you want to alternate the color between rows. E.g., To make an alternating list of blue and green names, you might say:

```
$names:blueListItem(),greenListItem()$
```

where presumably `blueListItem` template is an HTML `<table>` or something that lets you change background color. `names[0]` would get `blueListItem` applied to it, `names[1`] would get `greenListItem`, and `names2` would get `blueListItem` again, etc...

**Case 3.** At each iteration, StringTemplate pulls a single element from each of the expressions and passes them to template t or the anonymous template. Iteration proceeds while at least one of the attributes has values. Is an example that applies an anonymous template to two lists. Number of expressions must match the number of formal arguments in the template.

```
<names,phones:{ n,p | <n>: <p>}>
```

This is like the Python zip function that creates a list of tuples from multiple lists.

<a name="conditionals"></a>
## Conditionals

### Syntax

```
<if(boolexpr1)>subtemplate 
<elseif(boolexpr2)>subtemplate2 
...
<elseif(boolexprN)>subtemplateN 
<else>defaultsubtemplate 
<endif>
```

where

*boolexpr* → *boolexpr* `||` *boolexpr*<br>
*boolexpr* → *boolexpr* `&&` *boolexpr*<br>
*boolexpr* → `!`*boolexpr*<br>
*boolexpr* → *expr*

*boolexpr* is generally a simple attribute reference `a`, property reference `a.p`, or `!`*boolexpr*. Lowest to highest precedence order: `||` then `&&` then `!`.

### Discussion

The conditional expressions test of the presence or absence of an attribute. Strict separation of model and view requires that expressions cannot test attribute values such as `name=="parrt"`. If you do not set an attribute or pass in a null-valued attribute, that attribute evaluates to false. StringTemplate also returns false for empty lists and maps as well "empty" iterators such as 0-length lists (see `Interpreter.testAttributeTrue()`). All other attributes evaluate to true with the exception of Boolean objects. Boolean objects evaluate to their object value. Strictly speaking, this is a violation of separation, but it's just too weird to have Boolean false objects evaluate to true just because they are non-null.

Boolean expressions can use "or" and "and" operators though, again, I feel that it's a violation of model view separation. I decided to yield to lobbying efforts because we can already simulate these operators with nested conditionals. Use at your peril.

<a name="subtemplates"></a>
## Anonymous subtemplates

### Syntax

`{` *template* `}`<br>
`{` *arg1*`,` ...`,` *argN* `|` *template* `}`


### Discussion

Anonymous subtemplates are typically used for small or one-off templates that you need to apply to an attribute. They are literally templates without names and can have arguments used by the template expressions. For example, the following snippet converts a list of variable names to a list of integer variable definitions.

```
<vars:{v | int <v>;}>
```

If a subtemplate is applied to an attribute, it also has two hidden arguments: i and i0, which are the 1-base and 0-based iteration indexes, respectively. For example, the following expression converts a list of variable names to assignments: "a=1;b=2;".

```
<["a","b"]:{v | <v>=<i>;}>
```

Anonymous subtemplates are also useful to pass snippets to other templates. Given template:

```
method(name,body,cleanup) ::= <<
void <name>() {
  <body>
  <cleanup>
}
>>
```

we could invoke it with subtemplates:

```
<method(name="f", body={x=1;}, cleanup={printf("leaving <name>");})>
```

The output is

```
void f() {
  x=1;
  printf("leaving f");
}
```

Because of dynamic scoping, the snippet can see attribute name to fill in the string of the printf.

<a name=functions></a>
## Functions

StringTemplate has a number of side-effect free built-in functions that operate on attributes. Each function takes a single attribute and returns a single value. For the complete list of functions, see [StringTemplate cheat sheet](cheatsheet.md#functions).

<a name=lazy></a>
## Lazy evaluation

There is usually an order mismatch between convenient, efficient computation of data attributes and the order in which the results must be emitted according to the output language. The developer’s choice of controller and data structures has extensive design ramiﬁcations. If the developer decides to have the templates embody both view and controller, then the order of the output constructs drives output generation. This implies that the order of attribute ai references in the view dictates the order in which the model must compute those values, which may or may not be convenient. If the output language requires that n attributes be emitted in order a0..an−1, a single forward computation dependency, ai = f(aj) for i < j, represents a hazard. Each ai computation must manually trigger computations for each attribute upon which it is dependent. A simple change in the attribute reference order in the output templates can introduce new dependencies and unforeseen side-effects that will cause bad output or even generator crashes. This approach of having the templates drive generation by triggering computations and pulling attributes from the model is not only dangerous but may also make the computations inconvenient and inefﬁcient.

Decoupling the order of attribute computations from the order in which the results must be emitted is critical to avoiding dependency hazards--the controller must be separated from the view. A controller freed from the artiﬁcial ordering constraints of the output language may trigger computations in the order convenient to the internal data structures of the code generator. This choice implies that all attributes are computed a priori and merely pushed into the view for formatting. Driving attribute computation off the model is very natural, but computation results must be buffered up and tracked for later use by the view.

If the actual view (code emitter) is just a blob of print statements, the developer must build a special data structure just to hold the attributes temporarily whereas templates have built-in attribute tables where the controller can store attributes as they are created. The template then must know to delay evaluation until all attributes have been injected, effectively requiring a form of lazy evaluation. Because the controller computes all attributes a priori, however, StringTemplate can simply wait to evaluate templates until the controller explicitly renders the root template instance. This invocation performs a bottom-up recursive evaluation of all templates contained in t followed by template t itself.

In practice, delayed evaluation means that templates may be created and assembled as necessary without concern for the attributes they reference nor the order in which templates will be rendered to text. This convenience and safety has proven extremely valuable for complicated generators like that of ANTLR.

## Formal template syntax

Please see [StringTemplate template parser](https://github.com/antlr/grammars-v4/blob/master/stringtemplate/STParser.g4).
