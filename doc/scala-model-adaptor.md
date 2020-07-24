# A Model Adaptor for Scala Case Classes

One issue with the original adaptors is that they fail to access Scala case classes. In Scala, case classes of the following form are extremely convenient to use: 

```scala
  case class Point(x: Int, y: Int)
  case class Triangle(p1: Point, p2: Point, p3: Point)
```

Below, we have included one implementation of an adaptor that works with the aforementioned Scala case classes. The adaptor uses Scala reflection to fetch the desired property, and then converts it back to Java before passing it along to the interpreter. Of note, there is also a handy `register()` method in the companion object that allows for quicker and less verbose registering of a new adaptor to an `STGroup `. 

Note also that an adaptor for each case class (and each sub-class) must be registered for the template to render correctly. For example, to use the `Triangle` case class from above, you would have to register an adaptor for both the `Point` and `Triangle` classes.
```scala
import org.stringtemplate.v4._
import org.stringtemplate.v4.misc._

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

trait ToJava {
  import scala.collection.JavaConverters._

  /**
    * Recursively converts to java object that is usable by ST4
    * Note: For Options, None is converted to empty string ("")
    */
  def toObject(o: Any): Object = {
    o match {
      case opt: Option[_] => opt.fold[Object]("")(toObject)
      case map: Map[_, _] => map.map {
        case (k, v) => toObject(k) -> toObject(v)
      }.asJava
      case it: Iterable[_] => it.map(toObject).asJava
      case obj: AnyRef => obj
      case primitive => primitive.asInstanceOf[Object]
    }
  }
}

// A Scala-friendly adaptor for use with ST4
class ScalaModelAdaptor[T: TypeTag: ClassTag] extends ObjectModelAdaptor[T] with ToJava {
  import ScalaModelAdaptor._

  // stores seen fields/methods in mirrors for future reference
  private val mirrorCache = mutable.Map.empty[String, MethodMirror]

  // tells ST4 how to get the fields from an object of type T
  override def getProperty(interp: Interpreter, self: ST, model: T, property: Any, propertyName: String): Object = {
    mirrorCache.get(propertyName) match {
      case Some(mirror) => toObject(mirror.bind(model).apply())
      case _ =>
        typeOf[T].member(TermName(propertyName)) match {
          case NoSymbol => "" // skip over mismatched properties
          case refl =>
            val mirror = getMirror(refl, model)
            mirrorCache.put(propertyName, mirror)
            toObject(mirror.apply())
        }
    }
  }
}

object ScalaModelAdaptor {
  import scala.reflect.classTag

  // Registers a new ScalaModelAdaptor to a given STGroup
  def register[T: TypeTag: ClassTag](st: STGroup): STGroup = {
    val adaptor = new ScalaModelAdaptor[T]
    st.registerModelAdaptor(classTag[T].runtimeClass.asInstanceOf[Class[T]], adaptor)
    st
  }

  private def getMirror[T: TypeTag: ClassTag](refl: Symbol, model: T): MethodMirror = {
    scala.reflect.runtime.universe
      .runtimeMirror(getClass.getClassLoader)
      .reflect(model)
      .reflectMethod(refl.asMethod)
  }
}

// and a representative test:
object STest {  
  case class Point(x: Int, y: Int)
  case class PointClss(name: String, pointList: List[Point], pointMap: Map[Int, Point], pointSet: Set[Point], pointLL: List[List[Point]])

  val p = Point(4, 5)
  val q = Point(1, 6)
  val r = Point(3, 2)
  val pl = PointClss("Point Test", List(p, q ,r), Map(1 -> p, 2 -> q, 3 -> r), Set(p, q, r), List(List(p, q), List(q, r)))

  val group = {
    val template =
      """
        |pointPrinter(p) ::= "(<p.x>, <p.y>)"
        |
        |mapKeyVal(m) ::= <<
        |{<m.keys:{k | <k> -> <m.(k)>}; separator = ", ">}
        |>>
        |
        |ll(p) ::= <<
        |[<p:pointPrinter(); separator = ", ">]
        |>>
        |
        |test(t) ::= <<
        |<t.name>: 
        |List: [<t.pointList:pointPrinter(); separator = ", ">];
        |Map: <mapKeyVal(t.pointMap)>;
        |Set: {<t.pointSet:pointPrinter(); separator = ", ">};
        |List of list: [<t.pointLL:ll(); separator = ", ">]
        |>>
        |""".stripMargin
    val g = new STGroupString(template)

    ScalaModelAdaptor.register[PointClss](g)
    ScalaModelAdaptor.register[Point](g)
  }

  group.getInstanceOf("test").add("t", pl).render()

  /* should print: 
  Point Test:
  List: [(4, 5), (1, 6), (3, 2)];
  Map: {1 -> Point(4,5), 2 -> Point(1,6), 3 -> Point(3,2)};
  Set: {(4, 5), (1, 6), (3, 2)};
  List of list: [[(4, 5), (1, 6)], [(1, 6), (3, 2)]]
  */
}
```

## Advanced Customization for More Specific Adaptors

You can also extend this adaptor and override the `getProperty()` function to encode functionality that is specific to a certain case class. If the `toObject()` method is needed in the `getProperty()` override (for example, special casing with iterables, maps, or options), the new class can be extended with the `toJava` trait. For example, if you wanted to modify how the `x` and `y` values of the `Point` class outputted, you could write it like this: 

```scala
class PointAdaptor extends ScalaModelAdaptor[Point] with ToJava {
  override def getProperty(interp: Interpreter, self: ST, model: Point, property: Any, propertyName: String): Object = {
    propertyName match {
      case "x" => s"x: ${super.getProperty(interp, self, model, property, propertyName)}"
      case "y" => s"y: ${super.getProperty(interp, self, model, property, propertyName)}"
      case "x_times" =>
        val x = super.getProperty(interp, self, model, property, "x").asInstanceOf[Int]
        toObject(Seq.tabulate(x)(_ => model))
      case _ => super.getProperty(interp, self, model, property, propertyName)
    }
  }
}

object PointTest extends App {
  val p = Point(2, 5)

  val group = {
    val template =
      """
        |id(x) ::= "<x>"
        |point(p) ::= <<
        |(<p.x>, <p.y>); <p.x_times:id(); separator = ", ">
        |>>""".stripMargin
    val g = new STGroupString(template)
    g.registerModelAdaptor(classTag[Point].runtimeClass.asInstanceOf[Class[Point]], new PointAdaptor)
    g
  }

  group.getInstanceOf("point").add("p", p).render()
  // should render as: (x: 2, y: 5); Point(2,5), Point(2,5)
}
```
