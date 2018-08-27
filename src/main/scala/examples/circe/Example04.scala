package examples.circe

import io.circe.Decoder._
import io.circe._
import io.circe.generic.semiauto._  // <-
import io.circe.parser._
import io.circe.syntax._

import scalaz.Scalaz._

object Example04 extends App {

  case class User(id: Long, handle: String, display: String)
  case class Department (id: Long, users: List[User])

  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val departmentEncoder: Encoder[Department] = deriveEncoder[Department]
  implicit val departmentDecoder: Decoder[Department] = deriveDecoder[Department]

  "example 04.1:" |> println
  val userAdil = User(1, "adilakhter", "Adil Akhter")
  val department = Department(1, List(userAdil))

  assert(decode[User](userAdil.asJson.spaces2).right.get  == userAdil)

  "example 04.2:" |> println

  val dept = Department(1, List(userAdil))
  assert(decode[Department](dept.asJson.spaces2).right.get == dept)
}
