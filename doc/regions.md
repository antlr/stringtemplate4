# Template regions

ST introduces a finer-grained alternative to template inheritance, dubbed regions. (Regions are similar to a feature in Django). This feature allows a programmer to mark a location or series of lines in a template, and give it a name. A subgroup which inherits this template can provide replacement code to override just the named region. This avoids having to override the supergroup's template with a whole replacement template, when just a small addition or replacement is needed. While regions are syntactic sugar on top of template inheritance, the improvement in simplicity and clarity over normal coarser-grained inheritance is substantial.

**Warning about Regions and subdirectories**

Don't use regions in subdirectories; it's a bit broken and I'm having trouble nailing down exact semantics. Subject to change. So, if you put a template t in subdir S for fully-qualified name /S/t under a STGroupDir then don't use regions in t. Regions at root level are fine. Note I mean subdir not subgroup/inheritance stuff. That works.

## Add text at a location

For example, in a code-generation scenario, imagine using the following template called method to produce the text for a method:

```
// Java.stg
method(name,code) ::= <<
public void <name>() {
    <code>
}
>>
```

Suppose that you also want the option for the method template to place debugging statements into the generated method code. (To be clear about this example: this would be debugging code in the generated Java method, not code to debug the template processing itself.)

You could start placing debug text into the existing template, making it optional using the conditionally-included subtemplates feature, placing <if(...)> etc around the debugging lines. But that clutters up the templates of the Java group considerably, and also fails to achieve proper separation of concerns.

Instead you would like to have all debugging stuff encapsulated in a separate template group which focuses on debugging. In that template group, you could create an overriding template for method by copying and pasting the entire existing method template and inserting your additions. But then you are duplicating all of that output literal text, which breaks the "single point of change principle."

Instead just leave a hole in the main method template that a subgroup can override, here a location marked with `<@preamble()>`:

```
method(name,code) ::= <<
public void <name>() {
    <@preamble()>
    <code>
}
>>
```

In a template subgroup focusing on debugging (group dbg), define the region using a fully qualified name which includes the region's surrounding template name, `@method.preamble()`, and supply the replacement text:

```
// Dbg.stg
import "Java.stg"
@method.preamble() ::= <<System.out.println("enter");>>
```

Regions are like *subtemplates* scoped within a template, hence, the fully-qualified name of a region is `@t.r()` where `t` is the enclosing template and `r` is the region name.

## Replace a region of existing template text

Consider another problem where you would like, in a template subgroup, to replace a small portion of a large inherited template. Imagine you have a template that generates conditional statements in the output language, but you would also like to be able to generate a debug version of these statements which track the fact that an expression was evaluated.

(To be clear about this example, ths template's purpose is to produce "if" statements in the output language, here Java. That "if" is unrelated to the issue of using template `<if(...)>` expressions, which we are discussing how to avoid.)

Again, to avoid mingling debug version code with your main templates, you want to avoid "if dbg" type template expressions. Instead, mark the region within the template that might be replaced by an inheriting subgroup focusing on debugging. Here the code is marked with the pair of markers `<@eval>...<@end>`:

```
// Java.stg
test(expr,code) ::= "if (<@eval><expr><@end>) {<code>}"
```

where `<@r>..<@end>` marks the region called `r`. Now a template subgroup can override (replace) this region:

```
// Dbg.stg
import "Java.stg"
@test.eval() ::= "trackAndEval(<expr>)"
```

Regions may not have parameters, but because of the dynamic scoping of attributes, the overridden region may access all of the attributes of the surrounding template.

In an overridden region, `@super.r()` refers to the supergroup template's original region contents.

(I'm guessing this is trying to say: Within the replacement template text, ie: right-hand-side, you can use the symbol `@super.r()` to insert the original region contents.  Also guessing that `super` is a keyword, and should not be replaced, while `r` should be replaced with the actual region name. Pretty sure this needs to be enclosed in expression delimiters, not just bare. -- GW)
