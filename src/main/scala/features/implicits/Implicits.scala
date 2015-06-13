package features.implicits

// Example 1: TypeClass
object TypeClassApp {

  def lessThan[A: Ordering](a: A, b: A): Boolean = implicitly[Ordering[A]].lt(a, b)

  implicit val intOrdering = new Ordering[Int]{
    def compare(a: Int, b:Int): Int = a - b
  }

  val a = 10
  val b = 12

  val lesser = if(lessThan(a, b)) a else b


}


// Example 2: Class Extension
object ClassExtensionApp{
  implicit class HexableString(s: String) {
    def asHexVal: Seq[String] = s map { c =>
      f"0x$c%02x"
    }
  }

  // Pimp
  val haxVal = "implicit".asHexVal

}