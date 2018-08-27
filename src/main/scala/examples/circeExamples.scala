//package examples
//
//import java.util.Random
//
//import io.circe._
//import io.circe.generic.JsonCodec
//
//object CirceExamples04 extends App {
//  import io.circe._
//  import io.circe.parser._
//  import cats.implicits._
//
//
//  val transformValue: Json => Any = _.fold(
//    (),
//    bool => bool,
//    x => x.toInt.orElse(x.toLong).getOrElse(x.toDouble),
//    str => str,
//    array => array.map(transformValue),
//    obj => obj.toMap.transform((_: String, json: Json) => json.toString)
//  )
//
//  val string =
//    s"""{
//       |"fieldInt": ${Int.MaxValue},
//       |"fieldBoolean": true,
//       |"fieldDouble": ${Double.MaxValue},
//       |"fieldLong": ${Long.MaxValue},
//       |"fieldArray": [${Int.MaxValue}],
//       |"fieldMap": {
//       |    "fieldDouble" : 1.7976931348623157E308,
//       |    "fieldLong" : 9223372036854775807
//       | },
//       |"field2": "object2",
//       |"field3":"2017-01-01T12:00:00.000+02:00" }""".stripMargin
//
//
//  def foldJsonValue(jValue: Json): Any = jValue.fold(
//    (),
//    bool => bool,
//    x => x.toInt.orElse(x.toLong).getOrElse(x.toDouble),
//    str => str,
//    array => array.map(transformValue),
//    obj => foldJsonObject(obj)
//  )
//
//  def foldJsonObject(jObject: JsonObject): Map[String, Any] =
//    jObject.toMap.transform { (_, value: Json) => foldJsonValue(value) }
//
//  def convertJsonStringToMap(input: String): Map[String, Any]  = {
//    val a = parse(input)
//    val result: Option[Map[String, Any]] =
//    a.toOption
//      .flatMap(json => json.asObject)
//      .map(foldJsonObject)
//
//    println(a)
//
//    val r = result.get.map{case  (str, value) =>  s""""$str" ---> $value ---> ${value.getClass.getSimpleName}"""}.mkString("\n")
//
//    println(r)
//    Map.empty
//  }
//
//
//
//  convertJsonStringToMap(string)
//
//
//}
//
//
//object CirceExamples03 extends App {
//
//  import io.circe.parser._
//
//  def toBigInt(long: Long)               = (BigInt(long >>> 1) << 1) + (long & 1)
//  def toBigDecimal(long: Long)           = BigDecimal(toBigInt(long))
//
//  val random = new Random
//  val r = Seq(random.nextLong(), random.nextDouble(), toBigInt(Long.MaxValue)*Long.MaxValue, 0.1499999999999999944488848768742172978818416595458984375890065878)
//  val s = "1"
//
//  r.foreach { _r =>
//    val a1: Either[ParsingFailure, Json] = parse(_r.toString)
//    val className = a1.right.get.asNumber.get.getClass.getSimpleName
//
//    println(className)
//    println(_r)
//    println(a1.right.get.asNumber.get)
//  }
//  val a2 = parse(s)
//}
//
//object CirceExamples01 extends App {
//  import io.circe.generic.auto._
//  import io.circe.parser._
//  import io.circe.syntax._
//
//  import scalaz._, Scalaz._
//  val s = Map("featureSetMsg" -> 1, "scoringValue" -> 2.2)
//
//  sealed trait Foo
//  // defined trait Foo
//
//  case class Bar(xs: List[String]) extends Foo
//  // defined class Bar
//
//  case class Qux(i: Int, d: Option[Double]) extends Foo
//  // defined class Qux
//
//  val foo: Foo = Qux(13, Some(14.0))
//  // foo: Foo = Qux(13,Some(14.0))
//
//  foo.asJson.noSpaces |> println
//  // res0: String = {"Qux":{"i":13,"d":14.0}}
//
//  decode[Foo](foo.asJson.spaces4) |> println
//  // res1: Either[io.circe.Error,Foo] = Right(Qux(13,Some(14.0)))
//
//}
//
object CirceExamples02 extends App {
  import io.circe._
  import io.circe.generic.semiauto._
  import io.circe.syntax._
  import io.circe.parser._

  import scalaz._, Scalaz._

  case class Thing(foo: String, bar: Int)
  // defined class Thing

  implicit val encodeFoo: Encoder[Thing] = new Encoder[Thing] {
    final def apply(a: Thing): Json = Json.obj(
      ("foo2", Json.fromString(a.foo)),
      ("bar2", Json.fromInt(a.bar))
    )
  }
  // encodeFoo: io.circe.Encoder[Thing] = $anon$1@1866a8cd

  implicit val decodeFoo: Decoder[Thing] = new Decoder[Thing] {
    final def apply(c: HCursor): Decoder.Result[Thing] =
      for {

        f <- c.value.as[String]
        foo <- c.downField("foo2").as[String]
        bar <- c.downField("bar2").as[Int]
      } yield {
        Thing(foo, bar)
      }
  }


  val thing = Thing("test", 1)
  val thingString = thing.asJson.spaces2
  val thingFromJson = decode[Thing](thingString)
  println(thing.asJson)
  println(thingFromJson)


}
//
//object CirceExample10 extends App {
//  val json = """{ "@topicName":"topic", "@topicVersion":"1.0.0", "@maxLatency":13, "beName":"busEvt", "beVersion":"1.0.2", "modelName":"mdlName", "modelVersion":"1.0.4", "businessEventPayload":"{\"field1\":11,\"field2\":\"v32\",\"field3\":\"v23\" }", "featureSet":"{\"feat.ure1\":\"C\", \"feat.ure\":\"B\"}", "scoreModelType":"smt", "scoreResult":"ALERT", "scoreProbability":"sp", "scoreCategoryValues":"scv", "scoreConfidence":"sc", "scoreAffinity":"sa", "scoreAffinityRanking":"sar", "scoreEntityAffinity":"sea", "scoreEntityIdRanking":"seir", "scoreDescription":"Supicious Large Amount To Unknown Account", "scoreExplanation":"We don't trust you enough" }"""
//
//  import io.circe._
//  import io.circe.generic.semiauto._
//  import io.circe.syntax._
//  import io.circe.parser._
//
//
//  val jsonObj = parse(json)
//
//  println(jsonObj)
//
//
//}
//
//object CirceExample32 extends App {
//  @JsonCodec sealed trait EnumVal
//  case object not extends EnumVal
//  case object negate extends EnumVal
//  case object castToDouble extends EnumVal
//  case object castToLong extends EnumVal
//  case object castToString extends EnumVal
//  case object castToBoolean extends EnumVal
//  case object castToTimestamp extends EnumVal
//  case class UnaryExpression(operator: EnumVal, operand: String)
//
//  val a = UnaryExpression(castToBoolean, "test")
//
//  import io.circe.generic.auto._
//  import io.circe.syntax._
//  import io.circe.parser._
//
//  assert(decode[EnumVal](a.asJson.noSpaces).right.get == a)
//}
//
//object UnaryOperator {
//
//  sealed trait EnumVal
//
//  case object not extends EnumVal {
//    override val toString: String = "!"
//  }
//
//  case object negate extends EnumVal {
//    override val toString: String = "-"
//  }
//
//  case object castToDouble extends EnumVal {
//    override val toString: String = "(double)"
//  }
//
//  case object castToLong extends EnumVal {
//    override val toString: String = "(long)"
//  }
//
//  case object castToString extends EnumVal {
//    override val toString: String = "(string)"
//  }
//
//  case object castToBoolean extends EnumVal {
//    override val toString: String = "(boolean)"
//  }
//
//  case object castToTimestamp extends EnumVal {
//    override val toString: String = "(timestamp)"
//  }
//}
//
//
//
//object BinaryOperator {
//
//  sealed trait EnumVal
//
//  case object add extends EnumVal {
//    override val toString: String = "+"
//  }
//
//  case object subtract extends EnumVal {
//    override val toString: String = "-"
//  }
//
//  case object multiply extends EnumVal {
//    override val toString: String = "*"
//  }
//
//  case object divide extends EnumVal {
//    override val toString: String = "/"
//  }
//
//  case object lessThan extends EnumVal {
//    override val toString: String = "<"
//  }
//
//  case object greaterThan extends EnumVal {
//    override val toString: String = ">"
//  }
//
//  case object lessOrEqual extends EnumVal {
//    override val toString: String = "<="
//  }
//
//  case object greaterOrEqual extends EnumVal {
//    override val toString: String = ">="
//  }
//
//  case object equal extends EnumVal {
//    override val toString: String = "=="
//  }
//
//  case object notEqual extends EnumVal {
//    override val toString: String = "!="
//  }
//
//  case object and extends EnumVal {
//    override val toString: String = "&&"
//  }
//
//  case object or extends EnumVal {
//    override val toString: String = "||"
//  }
//
//}
//
//trait Expression
//case class Literal(s: String) extends Expression
//case class UnaryExpression(operator: UnaryOperator.EnumVal, expr: Expression) extends Expression
//
//abstract class FunctionExpression(protected val funcName: String, protected val minNrOfArgumentsRequired: Int, protected val maxNrOfArgumentsRequired: Int, protected val arguments: Seq[Expression]) extends Expression {}
//case class ContainsFunctionExpressions(override protected val arguments: Seq[Expression]) extends FunctionExpression("string_contains", 2, 2, arguments)
////case class SubstringFromFunctionExpressions(override protected val arguments: Seq[Expression]) extends FunctionExpression("substringFrom", 2, 2, arguments)
////case class BinaryExpression(operator: BinaryOperator.EnumVal, operand1: Expression, operand2: Expression)
////case class ReadDataRefExpression(readDataRef: String) extends Expression
////
////abstract class VariableReferenceExpression extends Expression {   def variableName: String }
////case class ExtendedVariableExpression(override val variableName: String) extends VariableReferenceExpression
////
//
//
//object MusasabiDSLExample extends App {
//  import io.circe._
//  import io.circe.jawn.decode
//  import io.circe.syntax._
//  import io.circe.generic.auto._
//  import cats.syntax.functor._
//
//  implicit val expressionEncoder: Encoder[Expression] = Encoder.instance {
//    case s@Literal(_) => s.asJson
//    case l@UnaryExpression(_,_) => l.asJson
//    case fe@ContainsFunctionExpressions(_) => fe.asJson
//  }
//  implicit val expressionDecoder: Decoder[Expression] = List[Decoder[Expression]](
//    Decoder[Literal].widen,
//    Decoder[UnaryExpression].widen,
//    Decoder[ContainsFunctionExpressions].widen
//  ).reduceLeft(_ or _)
//
//
//  val a: Expression = ContainsFunctionExpressions(Seq(Literal("test")))
//
//  println(a.asJson.noSpaces)
//  println(decode[Expression](a.asJson.noSpaces))
//
//
//  val t1 = UnaryExpression(UnaryOperator.not, Literal("test"))
//  val t1Decoded = decode[UnaryExpression](t1.asJson.noSpaces).right.get
//
//  println(t1)
//  println(t1Decoded)
//
//  assert ( t1 == t1Decoded)
//
//
//  val t2 = UnaryExpression(UnaryOperator.not, ContainsFunctionExpressions(Seq(Literal("test"))))
//  val t2Decoded = decode[UnaryExpression](t1.asJson.noSpaces).right.get
//
//  println(t2)
//  println(t2Decoded)
//
//}
//
//object CirceGenericDerivationApp extends App {
//
//  import io.circe._
//  import io.circe.generic._
//  import io.circe.generic.auto._
//  import io.circe.syntax._
//  import io.circe.parser._
//
//  @JsonCodec sealed trait A
//  case class B(b: String) extends A
//  case class C(c: String) extends A
//
//  object A
//
//  val c: A = C("1")
//  val b: A = B("1")
//
//  assert(decode[A](c.asJson.noSpaces).right.get == c)
//  assert(decode[A](b.asJson.noSpaces).right.get == b)
//}
//
//object CirceExample34 extends App {
//
////  import io.circe._
////  import io.circe.generic.semiauto._
////  import io.circe.syntax._
////  import io.circe.parser._
////
////  implicit val d = deriveDecoder[UnaryExpression]
////  implicit val l= deriveDecoder[Literal]
//
//  import io.circe._
//  import io.circe.jawn.decode
//  import io.circe.syntax._
//  import io.circe.generic.auto._
//  import cats.syntax.functor._
//
//  sealed trait Base
//  case class Foo(o: Option[Base]) extends Base
//  case class Bar(o: String) extends Base
//
////  implicit val fooEncoder: Encoder[Foo] = deriveEncoder
////  implicit val fooDecoder: Decoder[Foo] = deriveDecoder
////  implicit val barEncoder: Encoder[Bar] = deriveEncoder
////  implicit val barDecoder: Decoder[Bar] = deriveDecoder
//
////
////  implicit val baseEncoder: Encoder[Base] = Encoder.instance {
////    case s@Foo(_) => s.asJson
////    case l@Bar(_) => l.asJson
////  }
////  implicit val baseDecoder: Decoder[Base] = List[Decoder[Base]](
////    Decoder[Foo].widen,
////    Decoder[Bar].widen
////  ).reduceLeft(_ or _)
//
//
//
//  val t1 = Foo(Option(Foo(Option(Bar("rtest")))))
//  val t1Decoded = decode[Foo](t1.asJson.noSpaces).right.get
//
//  println(t1)
//  println(t1Decoded)
//
//  assert ( t1 == t1Decoded)
//
//}
//
//
//object CirceExample35 extends App {
//
//
//  import io.circe._
//  import io.circe.syntax._
//  import io.circe.generic.auto._
//  import io.circe.parser._
//  import cats.syntax.either._
//
//  case class Foo(value: String)
//  implicit val ke: KeyEncoder[Foo]  = KeyEncoder.instance(_.asJson.noSpaces)
//  implicit val kd: KeyDecoder[Foo]  = KeyDecoder.instance(str => decode[Foo](str).toOption)
//
//  val a: Map[Foo, String] = Map(Foo("test") -> "Test")
//
//
//  println(a.asJson)
//  println(decode[Map[Foo,String]](a.asJson.noSpaces))
//
//
//}
//
//object circeExample36 extends App {
//
//
//  import io.circe._
//  import io.circe.syntax._
//  import io.circe.generic.auto._
//  import io.circe.parser._
//  import cats.syntax.either._
//  import io.circe.Json.Folder
//
//  def transformJson(replacements: Map[String, String])= new Folder[Json] {
//    def onNull: Json = Json.Null
//    def onBoolean(bool: Boolean): Json = Json.fromBoolean(bool)
//    def onNumber(x: JsonNumber): Json = Json.fromJsonNumber(x)
//    def onString(str: String): Json = Json.fromString(replaceString(str, replacements))
//    def onArray(xs: Vector[Json]): Json =
//      Json.fromValues(xs.map(_.foldWith(this)))
//    def onObject(obj: JsonObject): Json = Json.fromFields(
//      obj.toMap
//        .map{case (k,v) => replaceProperty(k,v,replacements)}
//        .mapValues(_.foldWith(this)))
//  }
//
//  def replaceProperty(propertyName: String, value: Json, replacements: Map[String, String]): (String, Json) = {
//    val repeaterRegexp = """^(\w+)%%(\w+)%%$""".r
//    propertyName match {
//      case repeaterRegexp(nName, repeater) =>
//        val repeations: Seq[(String, String)] = getRepeater(replacements.getOrElse(s"%%$repeater%%", "0"))
//        val repResult = repeations.flatMap { rep =>
//          val transformedValue = value.foldWith(transformJson(replacements + ("%%@%%" -> rep._1) + (s"%%$repeater%%" -> rep._2)))
//          if (transformedValue.isArray) {
//            transformedValue.asArray.get
//          } else {
//            Seq(transformedValue)
//          }
//        }
//        (nName, Json.fromValues(repResult))
//      case _ => (propertyName, value)
//    }
//  }
//
//  def replaceString(text: String, replacements: Map[String, String]): String = {
//    val replacerRegexp = """(%%[^%]+%%)""".r
//
//    val matches = replacerRegexp.findAllMatchIn(text).toList
//    val tokens = matches.map(_.toString()).distinct
//    if (tokens.nonEmpty) {
//      val nStr = tokens.fold(text) {
//        case (acc, token) =>
//          acc.replace(token, replacements.getOrElse(token, token))
//      }
//      nStr
//    } else {
//      text
//    }
//  }
//
//  private def getRepeater(repVal: String): Seq[(String, String)] =
//    if (repVal.contains(';')) {
//      repVal.split(';').zipWithIndex.map { case (r, i) => ((65 + i).toChar.toString, r.trim) }
//    } else {
//      (0 until repVal.toInt).map(i => ((65 + i).toChar.toString, i.toString))
//    }
//
//
//
//  val text =
//    """{
//     | "version": "%%version%%",
//     | "nochange":[10,11,12],
//     | "obj": {
//     |  "noArray1%%repeat%%": 1,
//     |  "array2%%repeat%%": [1],
//     |  "array3%%repeat%%": [ {"%%@%%":"%%repeat%%"}],
//     |  "nochange1": "nochange1",
//     |  "nochange2": ["nochange2"]
//     | }
//     |}""".
//      stripMargin
//
//
//  //  val text = """{ "@topicName":"topic", "@topicVersion":"1.0.0", "@maxLatency":13, "beName":"busEvt", "beVersion":"1.0.2", "modelName":"mdlName", "modelVersion":"1.0.4", "businessEventPayload":"{\"field1\":11,\"field2\":\"v32\",\"field3\":\"v23\" }", "featureSet":"{\"feat.ure1\":\"C\", \"feat.ure\":\"B\"}", "scoreModelType":"smt", "scoreResult":"ALERT", "scoreProbability":"sp", "scoreCategoryValues":"scv", "scoreConfidence":"sc", "scoreAffinity":"sa", "scoreAffinityRanking":"sar", "scoreEntityAffinity":"sea", "scoreEntityIdRanking":"seir", "scoreDescription":"Supicious Large Amount To Unknown Account", "scoreExplanation":"We don't trust you enough" }"""
//
//  val json = parse(text).getOrElse(Json.Null)
//
//  //println(parse(text))
//
//  val replacements: Map[String, String] = Map("%%version%%"-> "AAA", "%%repeat%%"->"2")
//
//
//
//  val jsonModified = json.foldWith(transformJson(replacements))
//
//  val text2 = jsonModified.spaces2
//
//  println(text2)
//
//  println(parse("""{"version":"AAA","nochange":[10,11,12],"object":{"noArray1":[1,1],"array2":[1,1],"array3":[{"%%@%%":"0"},{"%%@%%":"1"}],"nochange1":"nochange1","nochange2":["nochange2"]}}""").getOrElse(Json.Null).spaces2)
//
//
//}