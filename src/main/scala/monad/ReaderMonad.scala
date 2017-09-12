package monad

import scalaz._
import Scalaz._
import scalaz.Kleisli._

case class Hero(name: String)

object Ooo {
  val finn = Hero("Finn")
  val jake = Hero("Jake")
}

case class Ooo(importantSettings: Unit) {
  import Ooo._

  def findHero(name: String): Option[Hero] =
    if (name == "Finn")
      Some(finn)
    else
      None

  def friendsRegistry(): Map[String, Hero] =
    Map(finn.name -> jake)

  def evalAdventure(hero1: Hero, hero2: Hero): String = {
    // Jake always saves the day, he's a magic dog!
    if (hero1 == jake || hero2 == jake) "awesome" else "disappointing"
  }
}

object OptionMonadAppl {
  object AdventureTime {
    def getHero(ooo: Ooo, name: String): Option[Hero] = ooo.findHero(name)

    def getBestFriend(ooo: Ooo, hero: Hero): Option[Hero] = {
      ooo.friendsRegistry().get(hero.name)
    }

    def goOnAdventure(ooo: Ooo, hero1: Hero, hero2: Hero): String = {
      val result: String = ooo.evalAdventure(hero1, hero2)

      result
    }
  }

}


object ReaderAppl {
  // Reader Monad basically encodes a simple function
  //
  // > type Reader[E, A] = ReaderT[Id, E, A]
  //
  // Reader is just a expression that take a parameter of type E
  // and returns a value of type A.


  // Think of it as follows:
  //
  // def func(e: E) : A = {
  //  // create some A using e
  // }
  // or
  // val func = (e: E) => {
  //  new A (e. foo())
  //
  type OooReader[T] = Reader[Ooo, T]

  object AdventureTimeWithReader {
    def hero(name: String): OooReader[Option[Hero]] = Reader {
      (ooo: Ooo) => ooo.findHero(name)
    }

    def bestFriend(hero: Hero): OooReader[Option[Hero]] = Reader {
      (ooo: Ooo) => ooo.friendsRegistry().get(hero.name)
    }

    def startAdventure(h1: Hero, h2: Hero): OooReader[String] = Reader{
      (ooo: Ooo) =>
        ooo.evalAdventure(h1, h2)
    }
  }
}


object ReaderTAppl{
  object AdventureTime{
    def hero(name: String) = kleisli[Option, Ooo, Hero]{
      (ooo: Ooo) => ooo.findHero(name)
    }

    def bestFriend(hero: Hero)  = kleisli[Option, Ooo, Hero]{
      _.friendsRegistry().get(hero.name)
    }

    def startAdventure(h1: Hero, h2: Hero) = kleisli[Option, Ooo, String]{
      (ooo: Ooo) =>
        Some(ooo.evalAdventure(h1, h2))
    }
  }
}