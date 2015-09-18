package patterns.iterator

import IteratorPattern._

/**
 * http://etorreborre.blogspot.nl/2011/06/essence-of-iterator-pattern.html
 */
object IteratorPattern {
  trait Functor[F[_]]{
    def fmap[A,B](f: A ⇒ B): F[A] ⇒ F[B]
  }

 trait Pointed[F[_]]{
   def point[A](a: ⇒ A): F[A]
 }

  object PointedList extends Pointed[List]{
    def point[A](a: ⇒ A)= List(a)
  }

  trait PointedFunctor[F[_]]{
    val functor: Functor[F]
    val pointed: Pointed[F]

    def point[A](a: ⇒ A): F[A] = pointed.point(a)

    def fmap[A, B](f: A ⇒ B): F[A] ⇒ F[B] = functor.fmap(f)
  }


  // Instead of applying function to the computed value
  // we suppose that the function is itself a computed value inside
  // container F, that is F[A ⇒ B].
  // We provide a method applic to apply a function to a value of F[A].
  trait Applic[F[_]] {
     def applic[A, B](f: F[A ⇒ B]): F[A] ⇒ F[B]
   }

  trait Applicative[F[_]]{
    val pointedFunctor: PointedFunctor[F]
    val applic: Applic[F]

    def functor: Functor[F] = new Functor[F]{
      override def fmap[A, B](f: A => B):  F[A] => F[B] = pointedFunctor fmap f
    }

    def pointed: Pointed[F] = new Pointed[F] {
      override def point[A](a: => A): F[A] = pointedFunctor point a
    }

    def fmap[A, B](f: A ⇒ B): F[A] ⇒ F[B] = functor.fmap(f)
    def point[A](a: ⇒ A): F[A] = pointed.point(a)
    def apply[A, B](f: F[A ⇒ B]): F[A] ⇒ F[B] = applic.applic(f)
  }
}

object ApplicEx1 extends App with Applic[Option]{
  class Market
  class Fruit

  val market = new Market()

  // If market is closed, pricer returns NONE, otherwise it returns
  // pricing function
  def pricer(market: Market): Option[Fruit ⇒ Double] = ???

  // Grow function possibly returns a Fruit
  def grow:  Option[Fruit] = ???

  def applic[Fruit, Double] = ???

  val pricingFunction = pricer(market)
  val fruit = grow

  // using applic intance we can compute the price of a fruit
  val price: Option[Double] = applic(pricingFunction).apply(fruit)

  // pricer(new Market())
}

object ListApplicative extends Applicative[List]{
  override val pointedFunctor: PointedFunctor[List] = new PointedFunctor[List] {
    override val functor: Functor[List] = new Functor[List]{
      override def fmap[A, B](f: (A) => B): (List[A]) => List[B] = (l: List[A]) ⇒ l.map(f)
    }

    override val pointed: Pointed[List] = new Pointed[List] {
      override def point[A](a: => A): List[A] = List(a)
    }
  }

  override val applic: Applic[List] = new Applic[List] {
    override def applic[A, B](f: List[(A) => B]): (List[A]) => List[B] = (l: List[A]) ⇒
      (l zip f) map (p ⇒ p._2 apply p._1)

    // another way to implement is following:
    // for { a ← l; func ← f} yield func(a)
  }
}