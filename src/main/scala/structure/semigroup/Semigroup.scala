package structure.semigroup


/**
 * Implementation of Semi-group. A semi-group must support one operation
 * and conforms to the associativity rule.
 * That is,
 * append(a, append (b, c)) = append(append(a, b), c)
 */
trait Semigroup[A] {
  def append(a: A, b: A): A
}


object Semigroup{
  implicit val stringSemigroup = new Semigroup[String] {
    override def append(a: String, b: String): String = a + b
  }

  implicit val intSemigroup = new Semigroup[Int] {
    override def append(a: Int, b: Int): Int = a * b
  }

  implicit  def optionInstances[A: Semigroup] = new Semigroup[Option[A]] {
    override  def append(a: Option[A], b: Option[A]): Option[A] =
      (a, b) match {
        case (Some(a1), Some(b1)) => Some(implicitly[Semigroup[A]].append(a1, b1))
        case (Some(_), None) => a
        case (None, Some(_)) => b
        case _ => None
      }
  }

  implicit def ToSemigroupOps[A: Semigroup](a: A): SemigroupSyntax[A] =
    new SemigroupSyntax[A] {
      def self: A = a
      def F: Semigroup[A] = implicitly[Semigroup[A]]
    }

//  implicit def ToSemigroupOps[A: Semigroup](a: Option[A]): SemigroupSyntax[Option[A]] =
//    new SemigroupSyntax[Option[A]] {
//      def self: Option[A] = a
//      def F: Semigroup[Option[A]] = implicitly[Semigroup[Option[A]]]
//    }
}


trait SemigroupSyntax[A] {
  def self: A
  def F: Semigroup[A]
  def |+|(b: A): A = append(b) // alias for append
  //
  def append(b: A): A = F.append(self, b)
}

/**
 * REPL OUTPUT
 * scala> import structure.semigroup._
 * import structure.semigroup._
 *
 * scala> "adil" |+| "akhther"
 * res0: String = adilakhther
 *
 * scala> 1 |+| 10
 * res0: Int = 10
 * */

