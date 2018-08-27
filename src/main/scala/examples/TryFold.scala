package examples

import scala.util._
import scalaz._, Scalaz._

object TryFold extends App {

  def f: Boolean = {
    if (scala.util.Random.nextInt() % 100 == 0)
      throw new Exception("Good Luck!")

    scala.util.Random.nextBoolean()
  }


  println(Try(f).toDisjunction.fold(ex => throw ex, b => if(b) None else Some(b)))
  println(Try(f).cata(b => if(b) None else Some(b), ex => throw ex))

}
