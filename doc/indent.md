# Auto-indentation

Properly-indented text is a very desirable generation outcome, but it is often difficult to achieve--particularly when the programmer must do this manually. StringTemplate automatically and naturally indents output by tracking the nesting level of all attribute expression evaluations and associated whitespace preﬁxes. For example, in the following slist template, all output generated from the `<statements>` expression will be indented by two spaces because the expression itself is indented.

```
slist(statements) ::= <<
{
  <statements>
}
>>
```

If one of the statement attributes is itself an slist then those enclosed statements will be indented four spaces. The auto-indentation mechanism is actually an implementation of an output filter that programmers may override to tweak text right before it is written.

StringTemplate performs auto indentation as the text gets emitted during rendering using class AutoIndentWriter, which is an implementation of a generic STWriter (interface, protocol, or nothing depending on the port implementation language).

To turn off auto indentation, tell StringTemplate to use NoIndentWriter by invoking the write(writer) method instead of the usual render method:

```java
StringWriter sw = new StringWriter();
NoIndentWriter w = new NoIndentWriter(sw);
st.write(w); // same as render() except with a different writer
String result = sw.toString();
```