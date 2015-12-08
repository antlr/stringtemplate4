# Automatic line wrapping

StringTemplate never automatically wraps lines--you must explicitly use the wrap option on an expression to indicate that StringTemplate should wrap lines in between expression elements. StringTemplate never breaks literals, but it can break in between a literal and an expression. the line wrapping is soft in the sense that an expression that emits text starting before the right edge will spit out that element even if it goes past the right edge. In other words, StringTemplate does not break elements to enforce a hard right edge. It will not break line between element and separator to avoid having for example a comma appear at the left edge. You may specify the line width as an argument to `render()` such as `st.render(72`). By default, `render()` does not wrap lines.

That said, if there's a newline in the literal to emit, it will wrap at the newline.

To illustrate the simplest form of line wrapping, consider a simple list of characters that you would like to wrap at, say, line width 3. Use the wrap option on the chars expression:

```
duh(chars) ::= "<chars; wrap>"
```

If you were to pass in a,b,c,d,e and called `render(3)`, you would see

```
abc
de
```

as output. `wrap` may also take an argument but it's default is simply a \n string.

To illustrate when you would need a non-default version for this parameter, imagine the difficult task of doing proper Fortran line wrapping. Here is a template that generates a Fortran function with a list of arguments:

```
func(args) ::= <<
       FUNCTION line( <args; separator=","> )
>>
```

Given parameters `a`..`f` as the elements of the `args` list, you would get the following output:

```
       FUNCTION line( a,b,c,d,e,f )
```

But what if you wanted to wrap lines at a width of 30? Simply use `render(30)` and specify that the expression should wrap using newline followed by six spaces followed by the `c` character, which can be used as the continuation character:

```
func(args) ::= <<
       FUNCTION line( <args; wrap="\n      c", separator=","> )
>>
       FUNCTION line( a,b,c,d,
      ce,f )
```

Similarly, if you want to break really long strings, use `wrap="\"+\n \""`, which emits a quote character followed by plus symbol followed by 4 spaces.

StringTemplate properly tracks newlines in the text omitted by your templates so that it can avoid emitting wrap strings right after your template has emitted a newline. StringTemplate also looks at your wrap string to find the (sole) \n character. Wrap strings are of the form A\nB and StringTemplate emits A\n first and then spits out the indentation as required by auto-indentation and then finally B. Again, imagine, the list of characters to emit, but now consider that the expression has been indented:

```
duh(chars) ::= <<
  <chars; wrap>
>>
```

With the same input `a`..`e` and `render(4)`, you would see the following output:

```
  ab
  cd
  e
```

What if the expression is not indented with whitespace but has some text to the left? Consider dumping out an array of numbers as a Java array definition:

```
array(values) ::= <<
int[] a = { <values; wrap, separator=","> };
>>
```

With numbers:

```
3,9,20,2,1,4,6,32,5,6,77,888,2,1,6,32,5,6,77,4,9,20,2,
1,4,63,9,20,2,1,4,6,32,5,6,77,6,32,5,6,77,3,9,20,2,1,
4,6,32,5,6,77,888,1,6,32,5
```

this template will emit (at width 40):

```
int[] a = { 3,9,20,2,1,4,6,32,5,6,77,888,
2,1,6,32,5,6,77,4,9,20,2,1,4,63,9,20,2,1,
4,6,32,5,6,77,6,32,5,6,77,3,9,20,2,1,4,6,
32,5,6,77,888,1,6,32,5 };
```

While correct, that is not particularly beautiful code. What you really want, is for the numbers to line up with the start of the expression; in this case under the first `3`. To do this, use the anchor option, which means StringTemplate should line up all wrapped lines with left edge of expression when wrapping:

```
array(values) ::= <<
int[] a = { <values; wrap, anchor, separator=","> };
>>
```

Adding that option generates the following output:

```
int[] a = { 3,9,20,2,1,4,6,32,5,6,77,888,
            2,1,6,32,5,6,77,4,9,20,2,1,4,
            63,9,20,2,1,4,6,32,5,6,77,6,
            32,5,6,77,3,9,20,2,1,4,6,32,
            5,6,77,888,1,6,32,5 };
```

One final complication. Sometimes you want to anchor the left edge of all wrapped lines in a position to the left of where the expression starts. For example what if you wanted to print out three literal values first such as `1,9,2`? Because StringTemplate can only anchor at expressions simply wrap the literals and your values expression in an embedded anonymous template (enclose them with `<{...}>`) and use the anchor on that embedded template:

```
data(a) ::= <<
int[] a = { <{1,9,2,<values; wrap, separator=",">}; anchor> };
>>
```

That template yields the following output:

```
int[] a = { 1,9,2,3,9,20,2,1,4,
            6,32,5,6,77,888,2,
            1,6,32,5,6,77,4,9,
            20,2,1,4,63,9,20,2,
            1,4,6 };
```

If there is both an indentation and an anchor, StringTemplate chooses whichever is larger.
WARNING: `separator` and `wrap` values are templates and are evaluated once before multi-valued expressions are evaluated. You cannot change the wrap based on, for example, `<i>`.

Default values are `wrap="\n"`, `anchor="true"` (any non-null value means `anchor`).
