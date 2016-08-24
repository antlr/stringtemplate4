# Model adaptors

StringTemplate lets you access the properties of injected attributes, but only if they follow the JavaBeans naming pattern ("getters") or are publicly visible fields. This works well if you control the attribute class definitions, but falls apart for some models. Some models, though, don't follow the getter method naming convention and so template expressions cannot access properties. To get around this, we need a model adaptor that makes external models look like the kind StringTemplate needs. If object o is of type T, we register a model adaptor object, a, for T that converts property references on o. Given `<o.foo>`, StringTemplate will ask a to get the value of property foo. As with renderers, a is a suitable adaptor if "o is instance of a's associated type". For the statically typed language ports, here are the interfaces:

```java
public interface ModelAdaptor {
    public Object getProperty(Interpreter interpreter, ST self, Object o, Object property, String propertyName)
        throws STNoSuchPropertyException;
}
``` 
 
|Property name type|
|------------------|
|Property names are usually strings but they don't have to be. For example, if o is a dictionary, the property could be of any key type. The string value of the property name is always passed to the renderer by StringTemplate.|

## Example 1
 
```java
class UserAdaptor implements ModelAdaptor {
    public Object getProperty(Interpreter interpreter, ST self, Object o, Object property, String propertyName)
        throws STNoSuchPropertyException
    {
        if ( propertyName.equals("id") ) return ((User)o).id;
        if ( propertyName.equals("name") ) return ((User)o).theName();
        throw new STNoSuchPropertyException(null, "User."+propertyName);
    }
}
 
public static class User {
    private int id; // ST can't see; it's private
    private String name;
    public User(int id, String name) { this.id = id; this.name = name; }
    public String theName() { return name; } // doesn't follow naming conventions
}
```

```java
String template = "foo(x) ::= \"<x.id>: <x.name>\"\n";
STGroup g = new STGroupString(template);
g.registerModelAdaptor(User.class, new UserAdaptor());
ST st = g.getInstanceOf("foo");
st.add("x", new User(100, "parrt"));
String expecting = "100: parrt";
String result = st.render();
```

|Inheriting from ObjectModelAdaptor|
|---|
|You can inherit your ModelAdaptor from ObjectModelAdaptor to leverage its ability to handle "normal" attributes. You can choose to override the results of any given property or to handle properties that would not normally be handled by the default ObjectModelAdaptor.|

## Example 2
 
```java
class UserAdaptor extends ObjectModelAdaptor {
    public Object getProperty(Interpreter interpreter, ST self, Object o, Object property, String propertyName)
        throws STNoSuchPropertyException
    {
        // intercept handling of "name" property and capitalize first character
        if ( propertyName.equals("name") ) return ((User)o).name.substring(0,1).toUpperCase()+((User)o).name.substring(1);
        // respond to "description" property by composing desired result
        if ( propertyName.equals("description") ) return "User object with id:" + ((User)o).id;
        // let "id" be handled by ObjectModelAdaptor
        return super.getProperty(interpreter,self,o,property,propertyName);
    }
}
 
public static class User {
    public int id; // ST can see this and we'll let ObjectModelAdaptor handle it
    public String name;  // ST can see this, but we'll override to capitalize
    public User(int id, String name) { this.id = id; this.name = name; }
}
```
 
```java
String template = "foo(x) ::= \"<x.id>: <x.name> (<x.description>)\"\n";
STGroup g = new STGroupString(template);
g.registerModelAdaptor(User.class, new UserAdaptor());
ST st = g.getInstanceOf("foo");
st.add("x", new User(100, "parrt"));
String expecting = "100: Parrt (User object with id:100)";
String result = st.render();
```
