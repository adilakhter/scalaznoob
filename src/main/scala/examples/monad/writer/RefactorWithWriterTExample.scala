package examples.monad.writer



object RefactorWithWriterTExample extends App {

  import RefactorWithWriterTExampleUtils._

//  val eventTime: String = ???
//  val message: Map[String, Any] = ???
//  val publicKey: String = ???

  val logMessages =
    LogMessages(Seq(
      LogMessage.point(warn("This is a warn")),
      LogMessage.point(debug("This is a debug"))
    ))

  logMessages.unsafePerformIO()

}

object RefactorWithWriterTExampleUtils {

  val dslContent = "dslContent"
  val signature = "signature"

  case class LogMessage(unsafePerformIO: () => Unit)
  case class LogMessages(seqMessages: Seq[LogMessage]) {
    def unsafePerformIO(): Unit =
      seqMessages.foreach(_.unsafePerformIO)
  }

  object LogMessage {
    def point[A](a: => Unit): LogMessage= LogMessage(() => a)
  }


  def warn (str: String) = println(s"Warn--$str")
  def debug(str: String) = println(s"Debug-$str")


}

