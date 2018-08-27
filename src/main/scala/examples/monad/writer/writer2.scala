package examples.monad.writer

object writer2 {

  case class Writer[W,A](a: A, w: W) {
    def map[B](f: A => B): Writer[W, B] =
      Writer(f(a), w)
  }


}
