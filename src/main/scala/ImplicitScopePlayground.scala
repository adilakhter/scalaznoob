object ImplicitScopePlayground extends App {
	import scalaz._
	import Scalaz._

	
	sealed trait Currency
	case object USD extends Currency
	case object JPY extends Currency
	case object AUD extends Currency
	case object INR extends Currency
	
	case class Amount(m: Map[Currency, BigDecimal])


	final val zeroAmount: Amount = Amount(Monoid[Map[Currency, BigDecimal]].zero) 


	implicit def AmountAdditionMonoid = new Monoid[Amount] {
    	val m = implicitly[Monoid[Map[Currency, BigDecimal]]]
    	def zero = zeroAmount
    	def append(m1: Amount, m2: => Amount) = Amount(m.append(m1.m, m2.m))
  	}

  	val a1 = Amount(Map(USD -> 100, JPY -> 200, INR -> 2000))
  	val a2 = Amount(Map(USD -> 120, JPY -> 200, INR -> 2000))

  	val a3 = a1 |+| a2

  	println(a3)


  	val m1 = implicitly[Monoid[Map[String, Int]]]

  	val m2 = Map("a" -> 1, "b" -> 2)
  	val m3 = Map("a" -> 2, "b" -> 4)

  	val result = m1.append(m2, m3)

  	println(result)
}