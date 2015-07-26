
object PeriodPoc extends App {
  sealed trait Period
  class Month extends Period
  class Year extends Period

  def month = new Month
  def year = new Year

  case class Per[T, P <: Period](value: T, period: P) {
    override def toString = s"$value per $period"
  }

  case class Amount(value: Int) {
    override def toString = s"€ $value"
  }


  implicit class  PimpedInt(value: Int){
    def per[P<:Period](period: P) = Per(Amount(value), period)
  }


  val y: Per[Amount, Year] = 1000 per year
  val z: Per[Amount, Month] = 20000 per month


  println(y)
  println(z)

  def calculate(x: Per[Amount, Year]): Per[Amount, Month] = (x.value.value / 12) per month

  println(calculate(y))
}