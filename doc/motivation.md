# Motivation and philosophy

## Why should I use StringTemplate?

StringTemplate ensures the separation of the specification of business logic and computation required to generate structured text from the specification of how that text is presented (i.e. its formatting).

For web developers, this translates to ensuring that a web page's business and persistence logic is separated from the the page's presentation.

For developers involved in code generation, this translates to ensuring generation and persistence logic is separated from output code structure. This separation directly supports the development of retargetable code generators.

Other benefits of model-view separation:

1. Encourages the development of templates that are reusable in similar applications
1. Unentangled templates serves as clear documentation of the generated code structure
1. The templates (and hence the output or view) can be changed independently. Using more than one set of such templates is the basic mechanism for retargetable code generation
1. Nonprogrammers can modify output structure because it's not threaded with code

## Philosophy

`StringTemplate` was born and evolved during the development of http://www.jGuru.com. The need for such dynamically-generated web pages has led to the development of numerous other template engines in an attempt to make web application development easier, improve flexibility, reduce maintenance costs, and allow parallel code and HTML development. These enticing benefits, which have driven the proliferation of template engines, **derive entirely from a single principle**: separating the specification of a page's business logic and data computations from the specification of how a page displays such information.

These template engines are in a sense a reaction to the completely entangled specifications encouraged by JSP (Java Server Pages), ASP (Active Server Pages) and, even ASP.NET. With separate encapsulated specifications, template engines promote component reuse, pluggable site "looks", single-points-of-change for common components, and high overall system clarity. In the code generation realm, model-view separation guarantees retargetability.

The normal imperative programming language features like setting variables, loops, arithmetic expressions, arbitrary method calls into the model, etc... are not only unnecessary, but they are very specifically what is wrong with ASP/JSP. Recall that ASP/JSP (and ASP.NET) allow arbitrary code expressions and statements, allowing programmers to incorporate computations and logic in their templates. A quick scan of template engines reveals an unfortunate truth--all but a few are Turing-complete languages just like ASP/JSP/ASP.NET. One can argue that they are worse than ASP/JSP/ASP.NET because they use languages peculiar to that template engine. Many tool builders have clearly lost sight of the original problem we were all trying to solve. We programmers often get caught up in cool implementations, but we should focus on what **should** be built not what **can** be built.

The fact that StringTemplate does not allow such things as assignments (no side-effects) should make you suspicious of engines that do allow it. The templates in ANTLR 3 and 4's code generators are vastly more complicated than the sort of templates typically used in web pages creation with other template engines yet, there hasn't been a situation where assignments were needed. If your template looks like a program, it probably is--you have totally entangled your model and view.

After examining hundreds of template files that I created over years of jGuru.com (and now in ANTLR v3) development, I found that I needed only the following four basic canonical operations (with some variations):

* attribute reference; e.g., <phoneNumber>
* template reference (like #include or macro expansion); e.g., <searchbox()>
* conditional include of subtemplate (an IF statement); e.g., <if(title)><title><title></title><endif>
* template application to list of attributes; e.g., <names:bold()> where template references can be recursive.

Language theory supports my premise that even a minimal StringTemplate engine with only these features is very powerful--such an engine can generate the context-free languages; see [Enforcing Strict Model-View Separation in Template Engines](http://www.cs.usfca.edu/~parrt/papers/mvc.templates.pdf). E.g., most programming languages are context-free as are any XML pages whose form can be expressed with a DTD.

While providing all sorts of dangerous features like assignment that promote the use of computations and logic in templates, many engines miss the key elements. Certain language semantics are absolutely required for generative programming and language translation. One is recursion. A template engine without recursion seems unlikely to be capable of generating recursive output structures such as nested tables or nested code blocks.

Another distinctive StringTemplate language feature lacking in other engines is *lazy-evaluation*. StringTemplate's attributes are lazily evaluated in the sense that referencing attribute "a" does not actually invoke the data lookup mechanism until the template is asked to render itself to text. Lazy evaluation is surprisingly  useful in both the web and code generation worlds because such order decoupling allows code to set attributes when it is convenient or efficient not necessarily before a template that references those attributes is created. For example, a complicated web page may consist of many nested templates many of which reference <userName>, but the value of userName does not need to be set by the model until right before the entire page is rendered to text via `render()`. You can build up the complicated page, setting attribute values in any convenient order.

StringTemplate implements a "poor man's" form of lazy evaluation by simply requiring that all attributes be computed a priori. That is, all attributes must be computed and pushed into a template before it is written to text; this is the so-called *push method* whereas most template engines use the *pull method*. The pull method appears more conventional because programmers mistakenly regard templates as programs, but pulling attributes introduces *order-of-computation dependencies*. Imagine a simple web page that displays a list of names (using some mythical Java-based template engine notation):

```
<html>
<body>
<ol>
$foreach n in names
  <li>$n</li>
$end
</ol>
There are $numberNames names.
</body>
</html>
```

Using the pull method, the reference to names invokes model.getNames(), which presumably loads a list of names from the database. The reference to numberNames invokes model.getNumberNames() which necessarily uses the internal data structure computed by getNames() to compute names.size() or whatever. Now, suppose a designer moves the numberNames reference to the `<title>` tag, which is **before** the reference to names in the foreach statement. The names will not yet have been loaded, yielding a null pointer exception at worst or a blank title at best. You have to anticipate these dependencies and have getNumberNames() invoke getNames() because of a change in the template.

I'm stunned that other template engine authors with whom I've spoken think this is ok. Anytime I can get the computer to do something automatically for me that removes an entire class of programming errors, I'll take it! Automatic garbage collection is the obvious analogy here.

The pull method requires that programmers do a topological sort in their minds anticipating any order that a programmer or designer could induce. To ensure attribute computation safety (i.e., avoid hidden dependency landmines), I have shown trivially in my academic paper that pull reduces to push in the worst case. With a complicated mesh of templates, you will miss a dependency, thus, creating a really nasty, difficult-to-find bug.

## StringTemplate mission

When developing StringTemplate, I recalled Frederick Brook's book, "Mythical Man Month", where he identified *conceptual integrity* as a crucial product ingredient. For example, in UNIX everything is a stream. My concept, if you will, is *strict model-view separation*. My mission statement is therefore:

> StringTemplate shall be as simple, consistent, and powerful as possible without sacrificing strict model-view separation.

I ruthlessly evaluate all potential features and functionality against this standard. Over the years, however, I have made certain concessions to practicality that one could consider as infringing ever-so-slightly into potential model-view entanglement. That said, StringTemplate still seems to enforce separation while providing excellent functionality.

I let my needs dictate the language and tool feature set. The tool evolved as my needs evolved. I have done almost no feature "backtracking". Further, I have worked really hard to make this little language self-consistent and consistent with existing syntax/metaphors from other languages. There are very few special cases and attribute/template scoping rules make a lot of sense even if they are unfamiliar or strange at first glance. Everything in the language exists to solve a very real need.

## StringTemplate language flavor

Just so you know, I've never been a big fan of functional languages and I laughed really hard when I realized (while writing the academic paper) that I had implemented a functional language. The nature of the problem simply dictated a particular solution. We are generating sentences in an output language so we should use something akin to a grammar. Output grammars are inconvenient so tool builders created template engines. Restricted template engines that enforce the universally-agreed-upon goal of strict model-view separation also look remarkably like output grammars as I have shown. So, the very nature of the language generation problem dictates the solution: a template engine that is restricted to support a mutually-recursive set of templates with side-effect-free and order-independent attribute references.

## Motivation for v4

Because of the weird license on ANTLR v2 (it's not BSD), I needed to remove StringTemplate's dependency on that library. That means I had to upgrade the grammars in StringTemplate to use ANTLR v3. I decided to completely reimplement StringTemplate for a v4 release because the old version is a complete mess on the inside since it grew organically.

Backward compatibility was the goal not the rule. I fixed the little quirks to make a very clean definition. It is also be a hell of a lot faster because I use the byte code interpreter instead of a tree-based interpreter.

If you're interested in interpreters, you might want to take a look at the compiler and bytecode interpreter I built for this version. It's very simple and clean:

[https://github.com/antlr/stringtemplate4](https://github.com/antlr/stringtemplate4)