# Attribute Renderers

The atomic element of a template is a simple attribute (object) that is rendered to text by its the appropriate string evaluation method for the port's language (toString, ToString, `_str_`, ...).
For example, an integer object is converted to text as a sequence of characters representing the numeric value. 
What if we want commas to separate the 1000's places like 1,000,000? 
What if we want commas and sometimes periods depending on the locale? 
For more, see [The Internationalization and Localization of Web Applications](http://www.cs.usfca.edu/~parrt/papers/i18n.pdf).

StringTemplate lets you register objects that know how to format or otherwise render attributes to text appropriately.
There is one registered renderer per type per group. 
In the statically type port languages like Java and C#, we use an interface to describe these renderers:
 
```
public interface AttributeRenderer<T> {
    public String toString(T value, String formatString, Locale locale);
}
```
 
To render expression `<e>`, StringTemplate looks for a renderer associated with the object type of `e`, say, *t*. 
If *t* is associated with a registered renderer, *r*, it is suitable and StringTemplate invokes the renderer method:
 
| Expression syntax | How interpreter invokes renderer r |
|-------------------|------------------------------------|
| `<e>`             | `r.toString(e, null, locale)`      |
| `<e; format="f">` | `r.toString(e, "f", locale)`       |

StringTemplate supplies either the default locale, or whatever locale was set by the programmer. 
If the format string passed to the renderer is not recognized then the renderer should simply call the usual string evaluation method.

To register a renderer, we tell the group to associate an object type with a renderer object. 
Here's an example that tells StringTemplate to render numbers with an instance of NumberRenderer using the Polish locale:
 
```java
String template =
    "foo(x,y) ::= << <x; format=\"%,d\"> <y; format=\"%,2.3f\"> >>\n";
STGroup g = new STGroupString(template);
g.registerRenderer(Number.class, new NumberRenderer());
ST st = group.getInstanceOf("foo");
st.add("x", -2100);
st.add("y", 3.14159);
String result = st.render(new Locale("pl"));
// resulted is " -2 100 3,142 " since Polish uses ' ' for ',' and ',' for '.'
```

**StringTemplate matches the types of expressions with the renderers using the "is instance of" relationship.**
As in this example, we registered a renderer for numbers and StringTemplate used it for subclasses such as integers and floating-point numbers. 
Here's the renderer definition:
 
```java
/** Works with Byte, Short, Integer, Long, and BigInteger as well as
 *  Float, Double, and BigDecimal.  You pass in a format string suitable
 *  for Formatter object:
 *
 *  http://java.sun.com/j2se/1.5.0/docs/api/java/util/Formatter.html
 *
 *  For example, "%10d" emits a number as a decimal int padding to 10 char.
 *  This can even do long to date conversions using the format string.
 */
public class NumberRenderer implements AttributeRenderer<Number> {
    public String toString(Number o, String formatString, Locale locale) {
        if ( formatString==null ) return o.toString();
        Formatter f = new Formatter(locale);
        f.format(formatString, o);
        return f.toString();
    }
}
```

You can register this renderer for `Number` or any subtype of it, but not unrelated types:

```java
STGroup g = ...;
g.registerRenderer(Number.class, new NumberRenderer()); // ok
g.registerRenderer(Integer.class, new NumberRenderer()); // ok
g.registerRenderer(Double.class, new NumberRenderer()); // ok
g.registerRenderer(String.class, new NumberRenderer()); // error
```

StringTemplate comes with three predefined renderers: `DateRenderer`, `StringRenderer`, and `NumberRenderer`.
