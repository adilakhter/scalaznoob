package examples
import scala.collection.immutable

object ValidationNelDemo extends App {

  import scalaz._, Scalaz._

  def toInts(maybeInts : List[String]): immutable.Seq[ValidationNel[Throwable, List[Int]]] = {
    val validationList = maybeInts map { s =>
      Validation.fromTryCatchNonFatal(s.toInt :: Nil).toValidationNel
    }

    validationList
  }


  toInts(List("1", "2", "3", "x", "z")) |> println






}
