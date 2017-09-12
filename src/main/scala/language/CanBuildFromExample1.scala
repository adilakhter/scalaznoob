package language

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable

object CanBuildFromExample1 extends App{

  def combineValues[U, T[_]](pairs: Seq[(String, U)])
                            (implicit  cbf: CanBuildFrom[T[U], U, T[U]]): Seq[(String, T[U])] = {
    val result = mutable.LinkedHashMap[String, mutable.Builder[U, T[U]]]()

    for ((name, value) ← pairs)
      result.getOrElseUpdate(name, cbf()) += value

    result.map{case (k, v) ⇒ k → v.result}.toSeq
  }

  val seqs = List("a" → "a", "a"→"b", "d" → "d")

  println("Result:"+ combineValues[String, List](seqs))
}
