package examples

object properConstantFunction extends App {
  // https://apocalisp.wordpress.com/2010/04/21/a-proper-constant-function-in-scala/


  def const[A, B](a: A) = (b: B) => a

  // problems
  // 1. b is always evaluated
  // 2. it is not properly quantified
  // > const(7)(error("too strict"))

  type Id[A] = A



  val x = 10

  def xToString = x.toString


}
