package applicatives


trait Applicative[A, F[_]]{
  def ap[B](f : F[A => B]): F[B]

}

object Applicative {

}
