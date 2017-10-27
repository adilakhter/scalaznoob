//package structure.monad
//
//
///**
// * Original source:
// * https://github.com/beezee/scalaz-writer-validation-example/blob/master/WriterExample.scala
// */
//object WriterExample1 extends App{
//  import scalaz._
//  import scalaz.Scalaz._
//
//  type Logger[A] = Writer[List[String], A]
//  type LoggedValidation[A] = Logger[Validation[String, A]]
//
//  case class User(age: Int, email: String)
//
//  def validateAge(age: Int): LoggedValidation[Int] =
//    (age > 19).fold(age.success, "Too young".failure).set(List("Checking age"))
//
//  def validateEmail(email: String): LoggedValidation[String] ={
//    val validated = for {
//      hasAt ← (email.contains("@")).fold(email.success, "Not a valid email".failure)
//      longEnough ← (email.length > 3).fold(email.success, "Too short".failure)
//    } yield  longEnough
//
//    validated.set(List("Checking email"))
//  }
//
//  def mkUser(age: Int, email: String): LoggedValidation[User] =
//    for{
//      vAge   ← validateAge(age)
//      vEmail ← validateEmail(email)
//    } yield (vAge |@| vEmail)(User(_,_))
//
//
//  val user1 = mkUser(17, "a").left
//}
//
//
///**
// * Usage:
//
// scala> WriterExample1.mkUser(17, "foo").run
// res7: scalaz.Id.Id[(List[String], scalaz.Validation[String,structure.monad.WriterMonadExample1.User])] = (List(Checking age, Checking email),Failure(Too youngNot a valid email))
// *
// */