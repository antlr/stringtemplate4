# Expression options

There are 5 expression options at the moment:

* `separator`. Specify text to be emitted between multiple values emitted for a single expression. For example, given a list of names, `<names>` spits them out right next to each other. Using a separator can put a comma in between automatically: `<names; separator=",">`. This is by far the most commonly used option. You can use `separator={...}` too.
* `format`. Used in conjunction with the `AttributeRenderer` interface, which describes an object that knows how to format or otherwise render an object appropriately. The `toString(Object,String,Locale)` method is used when the user uses the format option: `$o; format="f"$`. Renderers check the `formatName` and apply the appropriate formatting. If the format string passed to the renderer is not recognized, then it should simply call `toString()` on the attribute. 

  This option is very effective for locale changes and for choosing the display characteristics of an object in the template rather than encode. 

  Each template may have a renderer for each object type or can default to the group's renderer or the super group's renderer if the group doesn't have one. See [Object rendering](renderers.md#format).

* `null`. Emit a special value for each null element. For example, given `values=[9,6,null,2,null]`,
  ```
  $values; null="-1", separator=", "$
  ```
  emits:
  ```
  9, 6, -1, 2, -1
  ```
  See [Expressions](templates.md#expression-literals).
* `wrap`. Tell ST that it is okay to wrapped lines to get too long. The wrap option may also take an argument but it's default is simply a newline string. You must specify an integer width using the `render(int)` method to get ST to actually wrap expressions modified with this option. For example, given a list of names and expression `<names; wrap>`, a call to `render(72)` will emit the names until it surpasses 72 characters in with and then inserts a new line and begins emitting names again. Naturally this can be used in conjunction with the `separator` option. ST never breaks in between a real element and the separator; the wrap occurs only after a separator. See [Automatic line wrapping](wrapping.md).
* `anchor`. Line up all wrapped lines with left edge of expression when wrapping. Default is `anchor="true"` (any non-null value means anchor). See [Automatic line wrapping](wrapping.md).

The option values are all full expressions, which can include references to templates, anonymous templates, and so on. For example here is a separator that invokes another template:
```
$name; separator=bulletSeparator(foo=" ")+" "$
```

The wrap and anchor options are implemented via the [STWriter class](https://github.com/antlr/stringtemplate4/blob/master/src/org/stringtemplate/v4/STWriter.java). The others are handled during interpretation by ST. Well, the filters also are notified that a separator vs regular string is coming out to prevent newlines between real elements and separators.
