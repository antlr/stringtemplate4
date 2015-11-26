# null vs missing vs empty vs nonexistent in ST 4

*October 15, 2009*

## Part 1: Null-valued attributes

Let's consider values inside arrays. If names=`{"Tom", null, null, "Ter"}`, what should we get here:

```
<names>
```

or here

```
<names; separator=", ">
```

My preference would be: TomTer and Tom, Ter. That is what v3 does now. We recently introduced the null option so we can say:

```
<names; null="foo">
```

to get `foo` instead of an missing element when `names[i]` is null.

HOWEVER, you cannot set an attribute to null. So, if instead of passing the list, we set them individually, we get a different answer.

```java
st.add(names, "Tom");
st.add(names, null);  // do nothing
st.add(names, null);  // do-nothing
st.add(names, "Ter");
```

We get a list of {"Tom", "Ter"} sent to ST previously. All null values are ignored by add (actually called setAttribute in v3). The output would be "TomTer" even with null option. ooops.

I'm proposing that we allow null valued attributes in v4 to normalize the handling of single and multivalued attributes. In other words null and a list of one element with null in it should be the same.

## Part 2. Missing versus null versus non-null

In v4, I want to clearly identify the exact meetings of: missing versus null versus non-null means. Consider what this means:

```
<name>
```

There are three situations:

1. name doesn't exist as an attribute
1. name exists but has no value (it's null)
1. name exists and has a value

Similarly, what about properties (using getProp or isProp or the actual field name):

```
<user.name>
```

again, there are three situations:

1. name doesn't exist as a property of the user object
1. name exists but has no value (it's null)
1. name exists and has a value

Currently, <name> is no problem if it doesn't exist, but <user.name> throws an exception if name is not a valid property. The reason I did this was that it's okay to have an attribute you don't set but accessing a nonexistent field is most likely a programming error. (I think I'm going to set up a list of flags you can set in order to throw exceptions upon certain conditions, otherwise ST will be fairly permissive).

Anyway, given that we are going to allow null-valued attributes, plain old <name> could be missing, could be null, or could have a value. Given this, what does the following yield?

```
<name; null="foo">
```

Personally, I think it should be:

1. EMPTY if name doesn't exist as an attribute
1. foo if name exists but has no value (it's null)
1. name's value if name exists and has a value

So null option literally means the attribute exists but is null (has no value). If the attribute is simply missing, null option has no effect.

This is then consistent with lists and arrays. null applies to all null-valued elements because they exist physically in the list, they just have no value.

Ok, I think I just convinced myself that we'll allow null-valued attributes and that we will treat them differently than missing attributes. Secondly, `null` option only applies to present but null-valued attributes.
