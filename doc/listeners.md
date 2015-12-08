# Error listeners

To get notified when StringTemplate detects a problem during compilation of templates or, at runtime, when interpreting templates, provide StringTemplate with an error listener. The default listener sends messages to standard error/output, which are generally not what you want in a larger application. Here are the listener definitions in the various ports:
 
```java
public interface STErrorListener {
    public void compileTimeError(STMessage msg);
    public void runTimeError(STMessage msg);
    public void IOError(STMessage msg);
    public void internalError(STMessage msg);
}
```
 
The STMessage instances include information such as the ErrorType and any arguments. Evaluating the message to a string, as appropriate for the port language, yields a suitable message or you can pull it apart yourself.

You can specify a listener per group or per execution of the interpreter. To catch compile errors, make sure to set the listener before you trigger an action that processes the group file or loads templates:

```java
// listener per group
STGroup g = ...;
g.setListener(myListener);
g.getInstance("foo");
...
```

If you want to track interpretation errors with a particular listener, use the appropriate ST.write() method:

```java
// listener per rendering
STGroup g = ...;
ST st = g.getInstance("foo");
st.write(myWriter, myListener);
```

Imported groups automatically use the listener of the importing group.
