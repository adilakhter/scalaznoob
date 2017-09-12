package monad

import scalaz._
import Scalaz._
import scalaz.Kleisli._
import org.specs2.mutable.Specification

import scalaz.{Id, Kleisli}

class ReaderMonadSpec extends Specification {

  "AdventureTime with OptionMonad" should {
    "goOnWithAdventure with Some(awesome)" in {
      import OptionMonadAppl.AdventureTime._
      val ooo = Ooo(())

      val actual: Option[String] = for {
        hero1 <- getHero(ooo, "Finn")
        hero2 <- getBestFriend(ooo, hero1)
      } yield goOnAdventure(ooo, hero1, hero2)

      actual must equalTo(Some("awesome"))
    }

    "goOnWithAdventure with unknown hero results in None result " in {
      import OptionMonadAppl.AdventureTime._

      val ooo = Ooo(())

      val actual: Option[String] = for {
        hero1 <- getHero(ooo, "Unknown")
        hero2 <- getBestFriend(ooo, hero1)
      } yield goOnAdventure(ooo, hero1, hero2)

      actual must equalTo(None)
    }
  }

  "AdventureTime with ReaderMonad" should {
    "goOnWithAdventure with Some(awesome)" in {
      import ReaderAppl._
      import ReaderAppl.AdventureTimeWithReader._

      val ooo = Ooo(())
      val res: OooReader[String] = for {
        hero1 <- hero("Finn")
        hero2 <- bestFriend(hero1.get)
        result <- startAdventure(hero1.get, hero2.get)
      } yield result

      val actual: String = res.run(ooo)

      actual must equalTo("awesome")
    }
  }

  "AdventureTime with ReaderT" should {
    "goOnWithAdventure with Some(awesome)" in {
      import ReaderTAppl.AdventureTime._

      val ooo = Ooo(())

      val res: Kleisli[Option, Ooo, String] = for {
        hero1  <- hero("Finn")
        hero2  <- bestFriend(hero1)
        result <- startAdventure(hero1, hero2)
      } yield result

      val actual: Option[String] = res.run(ooo)

      actual must equalTo(Some("awesome"))
    }
  }

}
