package patterns.magnet


/**
 *  Example taken from
 *  http://derekwyatt.org/2014/01/11/scala-method-overloading-and-default-argument-values/
 */
object MagnetPatternEx1 {
  // def set (key: String, value: String) (implicit ttl: Duration = 1.hour): Int
  // def set (key: String, value: ByteString)(implicit ttl: Duration = 2.hour): Int

  // Use magnet pattern to avoid the error raised by compiler:
  // ... multiple overloaded alternatives of method set define default arguments.


  // In this example: return type does not vary with the magnet instance ⇒
  import akka.util.ByteString
  import scala.concurrent.duration._

  sealed trait ByteStringMagnet {
    val bs: ByteString
    val ttl: Duration
    def apply(f: (ByteString, Duration) ⇒ Int) : Int = f(bs, ttl)
  }

  object ByteStringMagnet {
    implicit def fromString(s: String)(implicit ttlValue: Duration = 1.hour): ByteStringMagnet =
      new ByteStringMagnet {
        val ttl = ttlValue
        val bs = ByteString(s)
      }

    implicit def fromByteString(bytes: ByteString)(implicit ttlLive: Duration = 2.hours): ByteStringMagnet =
      new ByteStringMagnet{
        val ttl = ttlLive
        val bs = bytes
      }
  }

  def set(key: String, magnet: ByteStringMagnet) : Int =
    magnet { (value, ttl) ⇒
      ttl.toHours.toInt
    }

//  Output :
// =================================================
//  scala> import patterns.magnet.MagnetPatternEx1._
//  import patterns.magnet.MagnetPatternEx1._
//
//  scala> import akka.util.ByteString
//  import akka.util.ByteString
//
//  scala> set ("key-1", ByteString("test"))
//  res3: Int = 2
//
//  scala> set ("key-2", "test")
//  res4: Int = 1
}



