package applicatives


trait Applicative[A, F[_]]{
  def ap[B](f : F[A => B]): F[B]

}

object Applicative {

}


object TestApplicative extends App {
	val f = (x: Int) ⇒ x + 1 

	val g = (x: Int) ⇒ (y: Int) ⇒ x + y 

	val gLifted = Option(5) map g // Option[Int ⇒ Int]

	val h = Option(10)
}

object WriterMonadEx{
	trait Monoid[A] {
		def append(a1: A, a2: A): A
		def empty: A
	}

	object Monoid {
		implicit def ListMonoid[A]: Monoid[List[A]] = new Monoid[List[A]] {
			def append(a1: List[A], a2: List[A]): List[A] = a1 ::: a2
			def empty = Nil 
		}
	}

	case class Logger[LOG, A](log: LOG, value: A) {
		def map[B](f: A => B) = Logger(log, f(value))
		
		def flatMap[B](f: A => Logger[LOG, B])(implicit m: Monoid[LOG]) = {
			val x: Logger[LOG, B] = f(value)
			Logger(m.append(log, x.log), x.value)

		}
	}

	object Logger {
		def unital[LOG, A](value: A)(implicit m: Monoid[LOG]) = 
			Logger(m.empty, value)
	}

	object Util {
		implicit def ListLogUtil[A](a: A) = new {
			def ~>[B](b: B): Logger[List[A], B] = Logger(List(a), b)
			def <|~[B](k: A => B): Logger[List[B], A] = Logger(List(k(a)), a)
		}

		def noLog[A](a: A) = 
			Logger.unital[List[String], A](a)
	}
}

object MainWriterMonadEx extends App{
	import WriterMonadEx._
	import Util._

	val x = 1 
	var r = 
		for(a <- addOne(x);
				b <- intString(a);
				c <- lengthIsEven(b);
				d <- noLog(hundredOrThousand(c));
				e <- times7(d)
			) yield e

	println("Result: "+ r.value)
	println
	println("LOG")
	println("---")

	r.log foreach println
	
	def addOne(n: Int): Logger[List[String], Int] = ("adding one to"+ n) ~> (n+1)

	def intString(n: Int): Logger[List[String], String] =
		("converting int to string" + n) ~> n.toString()

	def lengthIsEven(s: String): Logger[List[String], Boolean] =
		("checking length of "+ s + " for evenness") ~> (s.length % 2 == 0)

	def hundredOrThousand(b: Boolean): Int = // no logging
    if(b) 100 else 1000

  def times7(n: Int): Logger[List[String], Int] =
    (n * 7) <|~ ("multiplying " + n + " by 7 to produce " + _) 
}