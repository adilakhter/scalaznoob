package abstraction.cookiebox

/**
 * From blog post:
 * http://debasishg.blogspot.in/2015/06/baking-can-teach-you-bit-of.html
 */
object CookieBoxApp {

  trait MMonoid[T] {
    def zero: T

    def append(t1: T, t2: T): T
  }

  case class CookieBox(count: Int) {
    // let's assume n < count for simplicity
    def eat(n: Int): CookieBox = CookieBox(count - n)
  }

  object CookieBox{
    implicit val CookieBoxMonoid = new MMonoid[CookieBox] {
      val zero = CookieBox(0)
      def append (i: CookieBox, j: CookieBox) = CookieBox(i.count+j.count)
    }
  }

  def howManyCookies(gp: CookieBox, gm: CookieBox) =
      CookieBox(gp.eat(gp.count).count + gm.eat(gm.count).count)

  import CookieBoxApp._
  import CookieBox._

  def howMany[A: MMonoid](gm: A, gp: A): A =
    implicitly[MMonoid[A]].append(gm,gp)
}

/* REPL output:
------------------


scala> val gp = CookieBox(1)
gp: abstraction.cookiebox.CookieBoxApp.CookieBox = CookieBox(1)

scala> val gp = CookieBox(10)
gp: abstraction.cookiebox.CookieBoxApp.CookieBox = CookieBox(10)

scala> val gm = CookieBox(1)
gm: abstraction.cookiebox.CookieBoxApp.CookieBox = CookieBox(1)

scala> howMany(gp, gm)
res0: abstraction.cookiebox.CookieBoxApp.CookieBox = CookieBox(11)

scala>
 */