package examples

package object structype {

  import java.time.format.DateTimeFormatter
  import java.time.{Instant, ZoneOffset, ZonedDateTime}

  object TimestampWithOffset {
    val MinValue: TimestampWithOffset = TimestampWithOffset(Long.MinValue, 0)

    def nowUTC() = TimestampWithOffset(System.currentTimeMillis(), 0)

    lazy val pattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

    def parseIsoOffsetDateTime(iso: String): TimestampWithOffset = {
      val zonedDateTime = ZonedDateTime.from(pattern.parse(iso))
      val utc = zonedDateTime.toInstant.toEpochMilli
      val offset = zonedDateTime.getOffset.getTotalSeconds
      TimestampWithOffset(utc, offset * 1000)
    }

    implicit class DurationOps(duration: Long) {
      def +(timestamp: TimestampWithOffset): TimestampWithOffset = TimestampWithOffset(duration + timestamp.utc, timestamp.offset)

      def -(timestamp: TimestampWithOffset): TimestampWithOffset =
        TimestampWithOffset(duration - timestamp.utc, timestamp.offset) // TODO: Should we support this one?
    }

  }

  case class TimestampWithOffset(utc: Long, offset: Int) extends Ordered[TimestampWithOffset] {
    def +(duration: Long): TimestampWithOffset = TimestampWithOffset(this.utc + duration, this.offset)

    def -(duration: Long): TimestampWithOffset = TimestampWithOffset(this.utc - duration, this.offset)

    def -(other: TimestampWithOffset): Long = this.utc - other.utc

    def toIsoOffsetDateTime: String =
      Instant.ofEpochMilli(utc).atOffset(ZoneOffset.ofTotalSeconds(offset / 1000)).format(TimestampWithOffset.pattern)

    override def toString: String = s"Timestamp($utc,$offset)($toIsoOffsetDateTime)"

    /**
      * Compare two timestampswith offset, only if utc and offset field are equal, return 0.
      *
      * @param that The timestampwithoffset to compare
      * @return Returns x where: x < 0 when this < that x == 0 when this == that x > 0 when this > that
      */
    override def compare(that: TimestampWithOffset): Int = {
      val diff = this - that
      if (diff == 0L) this.offset - that.offset
      else if (diff < 0L) -1
      else 1
    }
  }
}
