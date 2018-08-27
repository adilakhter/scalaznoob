package examples.circe

import io.circe.Decoder._
import io.circe._
import io.circe.parser._
import io.circe.syntax._

import scalaz.Scalaz._
import scalaz._

object Example03 extends App {

  case class User(id: Long, handle: String, display: String)

  // Define an encoder
  implicit val userEncoder: Encoder[User] =
    Encoder.forProduct3("id", "handle", "display")(user =>
      (user.id, user.handle, user.display))

  // Define a decoder
  implicit val userDecoder: Decoder[User] =
    Decoder.forProduct3("id", "handle", "display")(User.apply)

  "example 03.1:" |> println
  val userAdil = User(1, "adilakhter", "Adil Akhter")
  val encodedJsonString = userAdil.asJson.spaces2

  val decoded = decode[User](encodedJsonString)
  val decodedUser = decoded.right.get // JUST DO NOT DO THIS IN PRODUCTION

  assert( decodedUser == userAdil)

  "example 03.2:" |> println
  case class Department (id: Long, users: List[User])
  implicit val departmentEncoder: Encoder[Department] =
    Encoder.forProduct2("id", "users")(dept => (dept.id, dept.users))

  Department(1, List(userAdil)).asJson |> println
}
