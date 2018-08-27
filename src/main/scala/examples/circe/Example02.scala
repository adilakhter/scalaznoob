package examples.circe

import io.circe.Decoder._
import io.circe._
import io.circe.syntax._
import io.circe.parser._

import scalaz._, Scalaz._

object Example02 extends App {

  case class User(id: Long, handle: String, display: String)

  // Define an encoder
  implicit val userEncoder: Encoder[User] = Encoder.instance(user =>
    Json.obj(
      "id" -> Json.fromLong(user.id),
      "handle" -> Json.fromString(user.handle),
      "display" -> Json.fromString(user.display)))

  // Define a decoder
  implicit val userDecoder: Decoder[User] = Decoder.instance { c =>
    val result: Result[User] =
      for {
        id          <- c.downField("id").as[Long]
        handle      <- c.downField("handle").as[String]
        displayName <- c.downField("display").as[String]
      } yield User(id, handle, displayName)
    result
  }

  "example 02.1:" |> println
  val userAdil = User(1, "adilakhter", "Adil Akhter")
  // ---------------------------------------
  val encoded: Json = userAdil.asJson
  // -------------------------------------------
  // Singature of asJson is as follows:
  // final def asJson(implicit encoder: Encoder[A]): Json
  // -------------------------------------------
  val encodedJsonString = encoded.spaces2

  val decoded = decode[User](encodedJsonString)
  val decodedUser = decoded.right.get // JUST DO NOT DO THIS IN PRODUCTION

  encodedJsonString |> println
  decodedUser |> println

  assert( decodedUser == userAdil)

  // ------------------
  // Problems:
  // --------------------
  // boilerplates
  // verbose
  // parallel hierarchy that is needed to handled.
  // For instance:

  case class Department (id: Long, users: List[User])

  implicit val departmentEncoder: Encoder[Department] = Encoder.instance(department  =>
    Json.obj(
      "id" -> Json.fromLong(department.id),
      "users" -> Json.arr(department.users.map(_.asJson):_*))
    )

  "example 02.2:" |> println
  Department(1, List(userAdil)).asJson |> println

}
