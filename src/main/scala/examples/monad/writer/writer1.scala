package examples.monad.writer


// Try 1
object writer1 extends App {

  case class Writer(v: Int, diary: String) {
    def flatMap(f: Int => Writer) =
      f(v) match {
        case Writer(result, d) => Writer(result, diary + "\n" + d)
      }

    def map(f: Int => Int) = Writer(f(v), diary)
  }

  val result =
    Writer(2, "two")
      .flatMap { i => new Writer(i + 1, "three") }
      .flatMap { i => new Writer(i * 4, "four") }

  println(result.diary)
}
