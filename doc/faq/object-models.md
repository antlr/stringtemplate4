# Altering property lookup for Scala

Sriram Srinivasan contributed this Scala code that lets you directly access scala properties. It's a model adapter that adds a lookup for a method called "foo()" when the property name is "foo". The problem is using classes in scala with the extremely convenient case class:

```scala
case class Person(name: String, salary: Double)
```

This creates a class of the form

```scala
class Person {  public String name(),  public String name(String)}
```

etc. 

Sriram could make scala to generate beans style accessors. For that he would have to do the much uglier:

```scala
case class Person(@BeanProperty name: String, @BeanProperty salary: Double)
```

Anyway, the following code shows how to create an adapter that works better with Scala.

```scala
import org.stringtemplate.v4._
import org.stringtemplate.v4.misc._
import scala.collection.JavaConversions._
class ScalaObjectAdaptor extends ObjectModelAdaptor {
 @throws(classOf[STNoSuchPropertyException])
 override
 def getProperty(interp: Interpreter, self:ST, o:Object, property:Object, propertyName:String): Object = {
   var value: Object = null
   val c = o.getClass
   if ( property==null ) {
     return throwNoSuchProperty(c.getName() + "." + propertyName)
   }
   // Look in cache for Member first
   var member = classAndPropertyToMemberCache.get(c, propertyName)
   if ( member == null ) {
     member = Misc.getMethod(c, propertyName)
   }
   if (member == null) {
     return toJava(super.getProperty(interp, self, o, property, propertyName))
   }
   try {
     member match {
       case m: java.lang.reflect.Method => toJava(Misc.invokeMethod(m, o, value))
       case f: java.lang.reflect.Field => toJava(f.get(o))
     }
   }
   catch {
     case _ => throwNoSuchProperty(c.getName() + "." + propertyName)
   }
 }
 // recursively convert scala collections (and nested collections) to nested java collections
 def toJava(o: Object): Object = {
   o match {
     case l:List[_] => {
     var lo = l.asInstanceOf[List[Object]]
     // Convert to a Scala list of java objects, then make into an array
     lo.map (elem => toJava(elem)).toArray
     }
     case m:Map[_,_] => {
     // convert map values to java values.
     var om = m mapValues (v => toJava(v.asInstanceOf[Object]))
     // return map as a java hash map
     mapAsJavaMap(om)
     }
     case s:Set[_] => s.asInstanceOf[Set[Object]].map(v => toJava(v)).toArray
     case _ => o // no change
   }
 }
}
```

```scala
// Test driver.
object STest {
   var template = """
                   |fld(f) ::= "<f.name> .... <f.ty>"
                   |il(i) ::= "'<i>'"
                   |prop(m) ::= "<m.name> @ <m.city>"
                   |clss(cls) ::= <<
                   |structure <cls.clsname> {
                   |  <cls.flds:fld();separator="\n">
                   |  dict[foo] = <cls.dict.foo>
                   |  dict[bar] = <cls.dict.bar>
                   |  <cls.intlist:il(); separator=" ">
                   |  Props: {
                   |    <cls.listmap:prop(); separator="\n">
                   |  }
                   |}
                   |>>""".stripMargin
 def main(args: Array[String]) = {
    var stg = new STGroupString(template)
    stg.registerModelAdaptor(classOf[Object], new ScalaObjectAdaptor())
   case class Fld(name: String, ty: String)
   case class Clss(clsname: String, flds: Set[Fld], dict: Map[String, Int], intlist: List[Int], listmap: List[Map[String, String]])
   var flds = Set(new Fld("x", "Int"), new Fld("y", "Float"))
   var dict = Map("foo" -> 1, "bar" -> 2)
   var intlist = List(1,2,3,4,5)
   var listmap = List(Map("name" -> "ter", "city" -> "sf"), Map("name" -> "sriram", "city" -> "berkeley"))
   var st = stg.getInstanceOf("clss")
   st.add("cls", new Clss("Foo", flds, dict, intlist, listmap))
   println(st.render())
   // Should print
   /*structure Foo {
        x .... Int
        y .... Float
        dict[foo] = 1
        dict[bar] = 2
        '1' '2' '3' '4' '5'
        Props: {
          ter @ sf
          sriram @ berkeley
        }
    }*/
 }
}
```