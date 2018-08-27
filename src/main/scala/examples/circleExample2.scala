//package examples
//
//import scala.reflect.ClassTag
//
//import io.circe.{Decoder, Encoder, Json}
//
//object CirceExamples022 extends App {
//  import io.circe.generic.auto._
//  import io.circe.parser._
//  import io.circe.syntax._
//
//  import scalaz._, Scalaz._
//
//  //labeledgeneric
//  sealed trait Foo
//  case class Bar(xs: List[String]) extends Foo
//  case class Qux(i: Int, d: Option[Double]) extends Foo
//
//  val foo: Foo = Qux(13, Some(14.0))
//  val bar: Foo = Bar(List("Test"))
//
//  foo.asJson.noSpaces |> println
//  decode[Foo](foo.asJson.spaces4) |> println
//
//  bar.asJson.noSpaces |> println
//  decode[Foo](bar.asJson.spaces4) |> println
//
//  case class Bar2(xs: List[String]) extends Foo
//  val bar2: Foo = Bar2(List("Test2"))
//  bar2.asJson.noSpaces |> println
//  decode[Foo](bar2.asJson.spaces4) |> println
//}
//
//
////object CirceExample023 extends App {
////
////  import io.circe.generic.auto._
////  import io.circe.parser._
////  import io.circe.syntax._
////  import cats.syntax.
////
////  protected def codecKindMap: Map[String, String] = Map.empty
////
////  private def codecKind[T](e: T): String =
////    codecKindMap.getOrElse(e.getClass.getCanonicalName, e.getClass.getSimpleName)
////
////  private def codecKind[T:ClassTag]: String = {
////    val clazz = implicitly[ClassTag[T]].runtimeClass
////    codecKindMap.getOrElse(clazz.getCanonicalName, clazz.getSimpleName)
////  }
////
////  implicit class TypeEncoderSyntax[T: Encoder](e: T) {
////    def encodeType: io.circe.Json = Json.obj(codecKind(e) -> e.asJson)
////  }
////
////  implicit class TypeDecoderSyntax[T : ClassTag](d: Decoder[T]) {
////    def prepareDecoder: Decoder[T] = d.prepare(_.downField(codecKind))
////  }
////
////  trait Identifier {
////    def name: String
////    def version: String
////  }
////
////  case class TopicIdentifier(override val name: String, override val version: String) extends Identifier
////
////  case class BusinessEventIdentifier(override val name: String, override val version: String) extends Identifier
////
////
////  implicit val identifierEncoder: Encoder[Identifier] = Encoder.instance {
////    case ti @ TopicIdentifier(_, _)         => ti.encodeType
////    case bi @ BusinessEventIdentifier(_, _) => bi.encodeType
////  }
////
////
////  implicit val identifierDecoder: Decoder[Identifier] =
////    List[Decoder[Identifier]](
////      Decoder[TopicIdentifier].prepareDecoder.widen,
////      Decoder[BusinessEventIdentifier].prepareDecoder.widen)
////    .reduceLeft(_ or _)
////
////
////
////
////
////
////
////}