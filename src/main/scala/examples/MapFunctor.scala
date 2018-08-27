package examples

object MapFunctorExample extends App {

  trait Functor[A, +M[_]] {
    def map[B](f: A => B): M[B]
  }

  case class MapFunctor[K, V](mapKV: Map[K, V])
    extends Functor[V, Map[K, ?]] {

    override def map[V2](f: V => V2): Map[K, V2] =
      mapKV map {
        case (k, v) => (k, f(v))
      }
  }


  println(MapFunctor(Map(1->1, 2->2, 3->3)).map(_ * 10))
}
