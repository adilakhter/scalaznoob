package examples.circe

import io.circe._
import io.circe.syntax._
import io.circe.Decoder._
import scalaz._, Scalaz._

object Example01 extends App {

  // a: Any



  case class User(id: Long, handle: String, display: Any) {
    def displayAsJson: Json  = ???
  }

  val a1 = User(1, "test", 1)
  val a2 = User(1, "test", "test")

  // Define an encoder
  // A => JSON
  val userEncoder: Encoder[User] = Encoder.instance(user =>
    Json.obj(
      "id" -> Json.fromLong(user.id),
      "handle" -> Json.fromString(user.handle),
      "display" -> user.displayAsJson))

  // Define a decoder
  val userDecoder: Decoder[User] = Decoder.instance { c =>
    val result: Result[User] =
      for {
        id          <- c.downField("id").as[Long]
        handle      <- c.downField("handle").as[String]
        displayName <- c.downField("display").as[String]
      } yield User(id, handle, displayName)
    result
  }

  // Example
  val userAdil = User(1, "adilakhter", "Adil Akhter")

  val encoded: Json = userEncoder(userAdil)
  val encodedJsonString = encoded.noSpaces
  //{"id":1,"handle":"adilakhter","display":"Adil Akhter"}

  val decoded: Result[User] = userDecoder.decodeJson(encoded)
  val decodedUser = decoded.right.get // JUST DO NOT DO THIS IN PRODUCTION

  assert( decodedUser == userAdil)

  "example 01.1:" |> println
  // ------------------
  // Problems:
  // --------------------
  // boilerplates.
  // verbose
  // parallel hierarchy that is needed to handled.
  // and wired during Json encoding and decoding
  // For instance:
  case class Department (id: Long, users: List[User])
}
