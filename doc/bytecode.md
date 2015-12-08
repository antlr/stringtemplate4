# Template to Bytecode mapping

## Expressions

|expression|bytecode|
|----------|--------|
|`<expr>`|expr<br>write|
|`<(expr)>`|expr<br>tostr <br>write|
|`<expr; o1=e1, o2=e2>`| expr <br> options <br> e1 <br> store\_option o1-option-index <br> e2 <br>store\_option o2-option-index <br> write\_opt<br>|
|text| load\_str string-pool-index|
|`true`| true|
|`false`| false|
|`a`| load\_local attribute-index     ; if a is template argument|
|`i`| load\_local attribute-index|
|`i0`| load\_local attribute-index|
|`a`| load\_attr a-string-pool-index|
|`a.b`| load\_attr a        ; from now now, a means its string index <br> load\_prop b|
|`a.(b)`| load\_attr a <br> load\_attr b <br> load\_prop\_ind|
|`t()`| new t,0          ; string pool index of t|
|`super.r()`| super\_new region\_t\_r,0 ; region r in template t|
|`t(e1,e2,e3)`| e1 <br> e2 <br> e3 <br> new t,3|
|`t(...)`| args <br>passthru t <br>new\_box\_args t|
|`t(a1=e1,a2=e2,a3=e3)`| args <br> e1 <br>store\_arg a1 <br>e2 <br>store\_arg a2 <br>e3 <br>store\_arg a3 <br>new\_box\_args t|
|`t(a1=e1,a2=e2,...)`| args<br>  e1<br> store\_arg a1 <br>e2<br> store\_arg a2<br> passthru t<br> new\_box\_args t|
|`(expr)(args)`| expr <br>tostr <br>args <br>new\_ind num-args|
|`a:t()`| load\_attr a <br>null <br>new t,1<br> map|
|`a:t(x)`| load\_attr a<br>null<br> x <br>new t,2<br> map|
|`a:t(),u()`| load\_attr a <br>null <br>new t,1<br> null <br>new u,1 <br>rot\_map 2|
|`a,b:t()`| load\_attr a <br>load\_attr b <br>null <br>null <br>new t,2 <br>zip\_map 2|
|`first(expr)`| expr <br>first        ; predefined function|
|`[`*a*`,`*b*`,`*c*`]`|list<br> a <br> add <br> b <br> add <br> c <br> add|

## Anonymous templates

|expression|bytecode|
|----------|--------|
|`{t}`|new \_subN,0|
|`a:{x | ...}`|load\_attr a<br> null<br> new \_subN, 1<br> map|
|`a,b:{x,y | ...}`|load\_attr a <br>load\_attr b<br> null<br> null<br> new \_subN,2<br> zip\_map 2|

## If statements

upon if, create 'end' label.<br>
upon else, create 'else' label.

`<if(a)>t<endif>`:

```
    load_attr a                                          
    brf end 
    t 
    write 
end:
```

`<if(a)>t<else>u<endif>`:

```
   load_attr a 
    brf else 
    t 
    write 
    br end 
else: 
    u 
    write 
end:
```

`<if(a)>t<elseif(b)>u<else>v<endif>`:

```
    load_attr a 
    brf lab1 
    t 
    write 
    br end 
lab1: 
    load_attr b 
    brf lab2 
    u 
    write 
    br end 
lab2: 
    v 
    write 
end:
```

`<if(!a)>t<endif>`:

```
    load_attr a 
    not 
    brf end 
    t 
    write 
end:
```

`a||b`:

```
    load_attr a 
    load_attr b 
    or
```

`a&&b`:

```
    load_attr a 
    load_attr b 
    and
```

## Auto-indentation

`<expr>`\n:

```
expr 
write 
newline
```

\n\t`<expr>`:

```
newline 
indent "\t" 
expr 
write 
dedent
```

## Size limitations

I use unsigned shorts not ints for the bytecode operands and addresses. This limits size of templates but not the output size. In single template, you can have only 64k of:

* attributes
* unique property name refs
* unique template name refs
* options (there are only about 5 now)
* lists or template names in a map/iteration operation
* bytecodes (short addressed)
* chunks of text outside of expressions. effectively same thing as saying can have at most 64k / n expressions where n is avg size of bytecode to emit an expression. E.g., 3 bytes to write a chunk of text.
