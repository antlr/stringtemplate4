# The real story on null vs empty

*December 27, 2012*

In [null vs missing vs empty vs nonexistent in ST 4](null-vs-empty-previous.md) a few years ago, I tried to resolve in my head the difference between a missing attribute, a null value, an array with no elements, and a string with no characters. I don't think I got it completely thought through and ST v4 might have some weird inconsistencies. This page is an attempt to finally write down all the cases and resolve exactly how things should work.

I think the general rule should be: *no complete <...> expression evaluation ever equals null*.  A lone x can have value null, but the resulting <x> evaluates to the empty string. A missing entry in a list like [a,,b] is the only way to create a null value in ST and I think this might have been a mistake on my part to allow missing elements.

Secondly, *an undefined attribute x or attribute property x.y is the same as null*. Note that undefined y gives a warning to the listener.

## Single-valued attribute values

This table shows the result of evaluating the indicated expression given the value of x:

|expr| x undefined | x null |x="" | x=list len=0|
|----|----|----|----|----|
|`<x>`	|""|	""|	""|	""|
|`<x:t()>`|	""|	""|	""	|""|
|`<x; null="y">`|	y	|y|	""|	""|
|`<x:t(); null="y">`	|y|	y|	""|	""|
|`<if(x)>y<endif>`	|""|	""|	y	|""|
|`<if(x)>y<else>z<endif>`	|z|	z|	y|	z|

For that, I assume that x.y where x and/or y are undefined is also considered the same as plain x is undefined or null.

I noticed that people are trying to do filtering inside ST expressions, which is not my intention as I think it violates model view separation. All filtering and computation must be done in the model.  The template is only to display the list not to compute the list. This brings us to what lists of values do.

## Multi-valued attribute values

We must distinguish between a list, array, or other Iteratable passed in as an attribute and what we create using the list [...] operator in ST expressions. Let's start with attributes passed in. x is an attribute  set to the value of the Java array construction specified by a surrounding Java application; a,b Java variables have string values "a", "b". t and u are templates that repeat their single argument.

|Expression|`x={}`|`x={a}`|`x={a,b}`|`x={null}`|`x={null,b}`|`x={a,null}`|`x={a,null,b}`|
|----|----|----|----|----|----|----|----|
|`<x>`|   ""|     a|      ab|     ""      |b|     a|      ab|
|`<x; null="y">`| ""      |a|     ab|     y|      yb|     ay|     ayb|
|`<x; separator=",">`     |""|    a|      a,b|    ""|     b|      a|      a,b|
|`<x; null="y", separator=",">`|  ""|     a|      a,b|    y|      y,b|    a,y|    a,y,b|
|`<if(x)>y<endif>`|       ""|     y|      y|      y|      y|      y|      y|
|`<x:{it \| <it>}>`|       ""|     a|      ab|     ""|     b|      a|      ab|
|`<x:{it \| <it>}; null="y">`|     ""|     y|      ab|     y|      yb|     ay|  ayb|
|`<x:{it \| <i>.<it>}>`|   ""|     1.a|    1.a2.b  |""|    1.b|    1.a|    1.a2.b|
|`<x:{it \| <i>.<it>}; null="y">`| ""|     1.a|    1.a2.b| y|      y2.b|   1.ay|   1.ay3.b|
|`<x:{it \| x<if(!it)>y<endif>}; null="z">`|       ""|     x|      xx|     z|      zx|     xz|     xzx|
|`<x:t():u(); null={y}>`| ""|      a|      ab|     y|      yb|     ay|     ayb|

Notice that a null value never gets passed as the iterated value.  Subtemplates are not applied to empty values. The null option is evaluated for null elements.

Currently, null option values are not counted in the `<i>` iterated index variable. **This would be a breaking change.**

Do we need a way to say null inside of template expressions? One thought might be as the default value for a template parameter, but null is the default value for a named parameter.  Parameter x has no value unless we set it.

## List construction in expressions

Ok, now let's look at [...] list construction within ST expressions. [...] cats lists together or creates a list from single-valued elements. [a,b] is the same as creating {a,b} in Java. If we have lists A,B then [A,B] is a single list with all the elements combined from A and B. I currently allow a missing element to mean the same thing as null so [a,,b] is the same as creating {a,null,b} in Java.
[ ] is a non-null list with no elements. `<[]>` is "", `<[]:t()>` is "". 

|expr|value|notes|
|----|----|----|
|`<[]>`|	""|	list is empty, yields empty string|
|`<[]; null="x">`|	""	|list is empty; no null element => no x|
|`<[[],[]]:{it \| <if(it)>x<endif>}; separator=",">`|	""	|`[[],[]]` collapses to `[]`|
|`<[]:t()>`	|""	|nothing to apply; template not evaluated|
|`<[]:{it \| <if(it)>x<endif>}>`|	""	|nothing to apply; template not evaluated|

Any missing map entry evaluates to null.
For dictionary `d ::= [x:"x"]`, `<d.(x):{it | <it>}>` is `x` and `<d.(y):{it | <it>}>` is `""`.

So, to summarize, I think we're ok as-is. I propose that we change null values to count in <i> index expressions if there is a null option in a future version like 4.1 but let's not change it on a point release like 4.0.7.
