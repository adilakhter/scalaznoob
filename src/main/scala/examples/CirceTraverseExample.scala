package examples

import scala.util.Random

import io.circe.Decoder.Result

object CirceTraverseExample extends App {
  import cats.syntax.either._
  import io.circe._, io.circe.parser._

  val json =
    """{"action":"upsert",
      |"var_name":"variabele",
      |"type":"String",
      |"fieldType":"ander type",
      |"actual_value":"waarde"}""".stripMargin



  val doc: Json = parse(json).getOrElse(Json.Null)

  println(doc)
  val cursor = doc.hcursor


  case class ManageStateAction(action: String, var_name: String, tpe: String, fieldType: String, actual_value: String, eventTime: Option[String])


  val r = cursor.downField("action").as[String].toOption

  val action: Result[String] = cursor.downField("action").as[String]
  val var_name: Result[String] = cursor.downField("var_name").as[String]
  val tpe: Result[String] = cursor.downField("type").as[String]
  val fieldType: Result[String] = cursor.downField("fieldType").as[String]
  val actualValue: Result[String] = cursor.downField("actual_value").withFocus { json => json.mapString(_.reverse)}.as[String]
  val eventTime: Result[Option[String]] = cursor.downField("eventTime").as[Option[String]]



  val result =
  for {
    at <- action
    vn <- var_name
    t <- tpe
    ft <- fieldType
    av <- actualValue
    et <- eventTime
  } yield   ManageStateAction(at, vn, t, ft, av, et)


  println(result.right.get)

val json2 =
  """[{"partyId":"123abc"},
     {"destinationAccountNr":"NL50INGB000123456789"}]"""

  val h = parse(json2).getOrElse(Json.Null).hcursor

  val  result2: Option[Seq[(String, Any)]] =
  h.values.map(xs =>
    xs.toSeq.flatMap(json => Map(Random.nextString(10) -> json.noSpaces)).toMap).map(_.toSeq)

  println(result2)

  println(h.downArray.downField("partyId").as[String])
  println(h.downArray.rightN(1).focus)
//  val res =
//for {
//  k <- h.downArray.as[String]
//  v <- h.downArray.as[String]
//} yield (k,v)

//  println(res)
}
