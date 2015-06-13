package features.implicits

object ImplicitsApplication {

  // Example 1: TypeClass
  def lessThan[A: Ordering](a: A, b: A): Boolean = implicitly[Ordering[A]].lt(a, b)

  implicit val intOrdering = new Ordering[Int]{
    def compare(a: Int, b:Int): Int = a - b
  }

  val a = 10
  val b = 12

  val lesser = if(lessThan(a, b)) a else b

}
