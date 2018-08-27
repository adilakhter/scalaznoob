package examples.monad.writer

// copied from https://gist.github.com/tjweir/3349857
object WriterTwithOptionExample extends App {

  import scalaz._
  import Scalaz._

  type OptionLogger[A] = WriterT[Option, NonEmptyList[String], A]
  val two: OptionLogger[Int] = WriterT.put(2.some)("The number two".wrapNel)
  val hundred: OptionLogger[Int] = WriterT.put(100.some)("One hundred".wrapNel)

 // val two_1:OptionLogger[Int] = 2.some.point[OptionLogger]

  val twoHundred = for {
    a <- two
    b <- hundred
  } yield a * b


  println(twoHundred.value) //Some(200)

  val res =
    twoHundred
      .written
      .map(_.toList)
      .getOrElse(List())

  res.foreach(println)
}
