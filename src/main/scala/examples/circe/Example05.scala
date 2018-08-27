package examples.circe

import examples.circe.Example02.User
import io.circe.Decoder.Result
import io.circe._
//<--
import io.circe.parser._
import io.circe.syntax._

import scalaz.Scalaz._

object Example05 extends App {

  import io.circe.generic.auto._


  case class TimeHelper2(mixed: Long) extends  AnyVal
  case class TimeHelper(private val mixed: Long) extends  AnyVal {
    def utc: Long = mixed >>8
    def offset: Long = mixed & 0xff
  }

  object TimeHelper {
    def apply(utc:Long, offset : Byte) = new TimeHelper((utc<<8) | offset)
  }

  val a = TimeHelper(1000, 10)
  val b = TimeHelper2(100)

  implicit val thEncoder: Encoder[TimeHelper] = Encoder.instance(timeHelper  =>
    Json.obj(
      "utc" -> Json.fromLong(timeHelper.utc),
      "offset" -> Json.fromLong(timeHelper.offset)))

  // Define a decoder
  implicit val thDecoder: Decoder[TimeHelper] = Decoder.instance { c =>
    val result =
      for {
        utc          <- c.downField("utc").as[Long]
        offset      <- c.downField("offset").as[Long]
      } yield TimeHelper(utc, offset.toByte)
    result
  }
  println(a.asJson.noSpaces) // { "utc":1000, "offset":10 }
  println(b.asJson.noSpaces) // { "utc":1000, "offset":10 }
//
//
//  "example 05.1:" |> println
//  val userAdil = User(1, "adilakhter", "Adil Akhter")
//  val department = Department(1, List(userAdil))
//
//  assert(decode[User](userAdil.asJson.spaces2).right.get  == userAdil)
//
//  "example 05.2:" |> println
//  val dept = Department(1, List(userAdil))
//  assert(decode[Department](dept.asJson.spaces2).right.get == dept)
//


}
