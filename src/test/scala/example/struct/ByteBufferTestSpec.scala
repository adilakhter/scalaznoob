package example.struct

package nl.ing.musasabi.core

import examples.structype.struct1.StructureType
import examples.structype.struct2.StructureType2
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}


class ByteBufferTestSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  val nrOfRuns = 100
  val nrOfCalcs = 100000L
  val dropLowest = 20
  val dropHighest = 20

  "Test What ByteBuffer Can Do" should {
    val valueLong: Any = 1L
    val valueBool: Any = false
    val valueString: Any = "SomeData"
    val valueBigDecimal: Any = BigDecimal("123131311313.121312")

    val structureDef = StructureType("MyStructure", Map("LongField" -> 1, "DoubleField" -> 2, "BooleanField" -> 3, "StringField" -> 4, "BigDecimalField" -> 5))
    val structureDef2 = StructureType2("MyStructure", Map("LongField" -> 1, "DoubleField" -> 2, "BooleanField" -> 3, "StringField" -> 4, "BigDecimalField" -> 5))
    val structValues = Map("LongField" -> valueLong, /* "DoubleField" -> 2.2,*/ "BooleanField" -> valueBool, "StringField" -> valueString, "BigDecimalField" -> valueBigDecimal)

    "convert Map to ByteArray" in {
      calculateTimingAverages("Map2ByteArray",
        (0 until nrOfRuns).map { r =>
          val start = System.nanoTime()
          (0L until nrOfCalcs).foreach { i =>
            val x = structureDef.toByteArray(structValues)
          }
          System.nanoTime() - start
        })
      calculateTimingAverages("Map2ByteArray",
        (0 until nrOfRuns).map { r =>
          val start = System.nanoTime()
          (0L until nrOfCalcs).foreach { i =>
            val x = structureDef2.toByteArray(structValues)
          }
          System.nanoTime() - start
        })
    }

    "convert ByteArray to Map" in {
      val x = structureDef.toByteArray(structValues)
      calculateTimingAverages("ByteArray2Map",
        (0 until nrOfRuns).map { r =>
          val start = System.nanoTime()
          (0L until nrOfCalcs).foreach { i =>
            val z = structureDef.toMap(x)
          }
          System.nanoTime() - start
        })
      val x2 = structureDef2.toByteArray(structValues)
      calculateTimingAverages("ByteArray2Map",
        (0 until nrOfRuns).map { r =>
          val start = System.nanoTime()
          (0L until nrOfCalcs).foreach { i =>
            val z = structureDef2.toMap(x2)
          }
          System.nanoTime() - start
        })
    }

    "test extract fields from byteArray" in {
      val x = structureDef.toByteArray(structValues)
      structureDef.get(x, "LongField") shouldBe valueLong
      structureDef.get(x, "BooleanField") shouldBe valueBool
      structureDef.get(x, "StringField") shouldBe valueString
      structureDef.get(x, "BigDecimalField") shouldBe valueBigDecimal
    }

    "extract single fields from byteArray" in {
      val x = structureDef.toByteArray(structValues)
      calculateTimingAverages("GetFieldFromByteArray",
        (0 until nrOfRuns).map { r =>
          val start = System.nanoTime()
          (0L until nrOfCalcs).foreach { i =>
            val x1 = structureDef.get(x, "LongField")
            val x2 = structureDef.get(x, "BooleanField")
            val x3 = structureDef.get(x, "StringField")
            val x4 = structureDef.get(x, "BigDecimalField")
          }
          System.nanoTime() - start
        })

      val x22 = structureDef2.toByteArray(structValues)
      calculateTimingAverages("GetFieldFromByteArray",
        (0 until nrOfRuns).map { r =>
          val start = System.nanoTime()
          (0L until nrOfCalcs).foreach { i =>
            val x1 = structureDef2.get(x22, "LongField")
            val x2 = structureDef2.get(x22, "BooleanField")
            val x3 = structureDef2.get(x22, "StringField")
            val x4 = structureDef2.get(x22, "BigDecimalField")
          }
          System.nanoTime() - start
        })
    }

    "extract single fields from Map" in {
      calculateTimingAverages("GetFieldFromMap",
        (0 until nrOfRuns).map { r =>
          val start = System.nanoTime()
          (0L until nrOfCalcs).foreach { i =>
            val x1 = structValues.get("LongField")
            val x2 = structValues.get("BooleanField")
            val x3 = structValues.get("StringField")
            val x4 = structValues.get("BigDecimalField")
          }
          System.nanoTime() - start
        })
    }
  }

  private def calculateTimingAverages(test: String, durations: Seq[Long]): (Double, Double, Double) = {
    val onlyCenters = durations.sortBy(x => x).slice(dropLowest, nrOfRuns - dropHighest)
    val averageDuration = onlyCenters.sum / (nrOfRuns - dropLowest - dropHighest).toDouble
    val minDelta = averageDuration - onlyCenters.min.toDouble
    val maxDelta = onlyCenters.max.toDouble - averageDuration
    println(f"$test: Average RunTime:\t${averageDuration / nrOfCalcs}%1.3f\tns per expresssion (\t-${minDelta / nrOfCalcs}%1.3f\t+${maxDelta / nrOfCalcs}%1.3f\t)")
    (averageDuration, minDelta, maxDelta)
  }
}