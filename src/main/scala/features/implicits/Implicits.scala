package features.implicits

// Example 1: TypeClass
object TypeClass{

  def lessThan[A: Ordering](a: A, b: A): Boolean = implicitly[Ordering[A]].lt(a, b)

  implicit val intOrdering = new Ordering[Int]{
    def compare(a: Int, b:Int): Int = a - b
  }

  val a = 10
  val b = 12

  val lesser = if(lessThan(a, b)) a else b


}


// Example 2: Class Extension
object ClassExtension{
  implicit class HexableString(s: String) {
    def asHexVal: Seq[String] = s map { c =>
      f"0x$c%02x"
    }
  }

  // Pimp
  val haxVal = "implicit".asHexVal
}


object InternalDsls{
  implicit class Recoverable[A](f: =>A){
    def recover(g: Throwable => A): A =
      try{
        f
      } catch {
        case t: Throwable => g(t)
      }
  }

  def thisThrows(): Int = throw new Exception("Argh!")

  val stable = thisThrows() recover { t =>
    if(t.getMessage == "Argh!") {
      10
    }else{
      5
    }
  }
}





/**
 * Other Usages:
 * - Decluttering code
 *
 *
 */