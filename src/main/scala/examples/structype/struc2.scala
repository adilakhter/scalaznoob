package examples.structype

import scala.annotation.tailrec
import scala.io.StdIn

import examples.structype.struct1.StructureType

object StructureType2ProfileApp extends App {

  val valueLong: Any = 1L
  val valueBool: Any = false
  val valueString: Any = "SomeData"
  val valueBigDecimal: Any = BigDecimal("123131311313.121312")

  val structureDef = StructureType("MyStructure", Map("LongField" -> 1, "DoubleField" -> 2, "BooleanField" -> 3, "StringField" -> 4, "BigDecimalField" -> 5))
  val structValues = Map("LongField" -> valueLong, /* "DoubleField" -> 2.2,*/ "BooleanField" -> valueBool, "StringField" -> valueString, "BigDecimalField" -> valueBigDecimal)

  Console.println("Waiting for Enter to start")
  StdIn.readLine()
  (0L until 1000000).foreach { i =>
    val x = structureDef.toByteArray(structValues)
  }
  Console.println("Waiting for Enter to Stop")
  StdIn.readLine()
}

object struct2 {

  object StructureType2 {
    val TypeNone: Byte = 0x01
    val TypeBooleanFalse: Byte = 0x02
    val TypeBooleanTrue: Byte = 0x03
    val TypeLong: Byte = 0x04
    val TypeDouble: Byte = 0x05
    val TypeTimestamp: Byte = 0x06
    val TypeString: Byte = 0x07
    val TypeBigDecimal: Byte = 0x08
  }

  object ByteArrayHelper {
    def get(ba: Array[Byte], idx: Int): Byte = {
      ba(idx)
    }

    def put(ba: Array[Byte], idx: Int, byte: Byte): Unit = {
      ba(idx) = byte
    }

    def getShort(ba: Array[Byte], idx: Int): Short = {
      ((ba(idx) << 8) |
        (ba(idx + 1) & 0xff)).toShort
    }

    def putShort(ba: Array[Byte], idx: Int, short: Short): Unit = {
      ba(idx) = (short >> 8).toByte
      ba(idx + 1) = short.toByte
    }

    def getInt(ba: Array[Byte], idx: Int): Int = {
      (ba(idx) << 24) |
        ((ba(idx + 1) & 0xff) << 16) |
        ((ba(idx + 2) & 0xff) << 8) |
        ba(idx + 3) & 0xff
    }

    def putInt(ba: Array[Byte], idx: Int, int: Int): Unit = {
      ba(idx) = (int >> 24).toByte
      ba(idx + 1) = (int >> 16).toByte
      ba(idx + 2) = (int >> 8).toByte
      ba(idx + 3) = int.toByte
    }

    def getLong(ba: Array[Byte], idx: Int): Long = {
      (ba(idx).toLong << 56) |
        ((ba(idx + 1).toLong & 0xff) << 48) |
        ((ba(idx + 2).toLong & 0xff) << 40) |
        ((ba(idx + 3).toLong & 0xff) << 32) |
        ((ba(idx + 4).toLong & 0xff) << 24) |
        ((ba(idx + 5).toLong & 0xff) << 16) |
        ((ba(idx + 6).toLong & 0xff) << 8) |
        ba(idx + 7).toLong & 0xff
    }

    def putLong(ba: Array[Byte], idx: Int, long: Long): Unit = {
      ba(idx) = (long >> 56).toByte
      ba(idx + 1) = (long >> 48).toByte
      ba(idx + 2) = (long >> 40).toByte
      ba(idx + 3) = (long >> 32).toByte
      ba(idx + 4) = (long >> 24).toByte
      ba(idx + 5) = (long >> 16).toByte
      ba(idx + 6) = (long >> 8).toByte
      ba(idx + 7) = long.toByte
    }

    def getDouble(ba: Array[Byte], idx: Int): Double =
      java.lang.Double.longBitsToDouble(getLong(ba, idx))

    def putDouble(ba: Array[Byte], idx: Int, double: Double): Unit =
      putLong(ba, idx, java.lang.Double.doubleToRawLongBits(double))

    def getChar(ba: Array[Byte], idx: Int): Char = {
      ((ba(idx) << 8) |
        (ba(idx + 1) & 0xff)).toChar
    }

    def putChar(ba: Array[Byte], idx: Int, char: Char): Unit = {
      ba(idx) = (char >> 8).toByte
      ba(idx + 1) = char.toByte
    }

  }

  case class StructureType2(structName: String, fieldIdentifiers: Map[String, Short]) {

    def get(byteArray: Array[Byte], fieldName: String): Any = {
      val lookupFieldId = fieldIdentifiers(fieldName)
      getFieldValueById(byteArray, 0, lookupFieldId) match {
        case Some(value) => value
        case None => None
      }
    }

    def toMap(byteArray: Array[Byte]): Map[String, Any] = {
      traverseFields(byteArray, 0, Seq.empty).toMap
    }

    @tailrec
    private def getFieldValueById(bb: Array[Byte], idx: Int, lookupFieldId: Short): Option[Any] = {
      if (idx < bb.length) {
        val fieldId = ByteArrayHelper.getShort(bb, idx)
        val typeId = ByteArrayHelper.get(bb, idx + 2)
        if (fieldId == lookupFieldId) {
          val (value, _) = getValueAndSizeByTypeId(bb, idx + 3, typeId)
          Some(value)
        } else {
          val skipSize = skipValueByTypeId(bb, idx + 3, typeId)
          getFieldValueById(bb, idx + 3 + skipSize, lookupFieldId)
        }
      } else {
        None
      }
    }

    @tailrec
    private def traverseFields(bb: Array[Byte], idx: Int, traversedFields: Seq[(String, Any)]): Seq[(String, Any)] = {
      if (idx < bb.length) {
        val fieldId = ByteArrayHelper.getShort(bb, idx)
        val fieldName = fieldIdentifiers.find { case (_, id) => id == fieldId }.map { case (name, _) => name }.getOrElse(s"unknown-$fieldId")
        val typeId = ByteArrayHelper.get(bb, idx + 2)
        val (fieldValue, size) = getValueAndSizeByTypeId(bb, idx + 3, typeId)
        traverseFields(bb, idx + 3 + size, traversedFields :+ fieldName -> fieldValue)
      } else {
        traversedFields
      }
    }

    private def skipValueByTypeId(bb: Array[Byte], idx: Int, typeId: Byte): Int = {
      typeId match {
        case StructureType2.TypeNone => 0
        case StructureType2.TypeBooleanFalse => 0
        case StructureType2.TypeBooleanTrue => 0
        case StructureType2.TypeLong => 8
        case StructureType2.TypeDouble => 8
        case StructureType2.TypeTimestamp => 12
        case StructureType2.TypeString => //i4size, c2[size]
          val size = ByteArrayHelper.getInt(bb, idx)
          4 + size * 2
        case StructureType2.TypeBigDecimal => //i4Scale, i4size, i1[size]
          val size = ByteArrayHelper.getInt(bb, idx + 4)
          4 + 4 + size
      }
    }

    private def getValueAndSizeByTypeId(bb: Array[Byte], idx: Int, typeId: Byte): (Any, Int) = {
      val fieldValue = typeId match {
        case StructureType2.TypeNone => (None, 0)
        case StructureType2.TypeBooleanFalse => (false, 0)
        case StructureType2.TypeBooleanTrue => (true, 0)
        case StructureType2.TypeLong => (ByteArrayHelper.getLong(bb, idx), 8)
        case StructureType2.TypeDouble => (ByteArrayHelper.getDouble(bb, idx), 8)
        case StructureType2.TypeTimestamp =>
          val utc = ByteArrayHelper.getLong(bb, idx)
          val offset = ByteArrayHelper.getInt(bb, idx + 8)
          (TimestampWithOffset(utc, offset), 12)
        case StructureType2.TypeString =>
          val x = new StringBuilder
          val size = ByteArrayHelper.getInt(bb, idx)
          (0 until size).map { i =>
            x.append(ByteArrayHelper.getChar(bb, idx + 4 + i * 2))
          }
          (x.toString(), 4 + size * 2)
        case StructureType2.TypeBigDecimal =>
          val scale = ByteArrayHelper.getInt(bb, idx)
          val size = ByteArrayHelper.getInt(bb, idx + 4)
          val arr: Array[Byte] = (0 until size).map(i => ByteArrayHelper.get(bb, idx + 8 + i)).toArray
          (BigDecimal(BigInt(arr), scale), 8 + size)
      }
      fieldValue
    }

    def toByteArray(fieldvalues: Map[String, Any]): Array[Byte] = {
      val byteArrayStructure: Iterable[(Short, Byte, Int, Any)] = getByteArrayStructure(fieldvalues)

      val byteArraySize = byteArrayStructure.map(_._3 + 3).sum

      val byteArray = new Array[Byte](byteArraySize)
      buildByteArray(byteArrayStructure.iterator, byteArray, 0)
      byteArray
    }

    @tailrec
    private def buildByteArray(byteArrayStructure: Iterator[(Short, Byte, Int, Any)], bb: Array[Byte], idx: Int): Unit = {
      if (byteArrayStructure.hasNext) {
        val (fieldId, typeId, len, fieldValue) = byteArrayStructure.next()
        ByteArrayHelper.putShort(bb, idx + 0, fieldId)
        ByteArrayHelper.put(bb, idx + 2, typeId)
        (typeId, fieldValue) match {
          case (StructureType2.TypeNone, _) =>
          case (StructureType2.TypeBooleanFalse, false) =>
          case (StructureType2.TypeBooleanTrue, true) =>
          case (StructureType2.TypeLong, v: Long) =>
            ByteArrayHelper.putLong(bb, idx + 3, v)
          case (StructureType2.TypeDouble, v: Double) =>
            ByteArrayHelper.putDouble(bb, idx + 3, v)
          case (StructureType2.TypeTimestamp, v: TimestampWithOffset) =>
            ByteArrayHelper.putLong(bb, idx + 3, v.utc)
            ByteArrayHelper.putInt(bb, idx + 11, v.offset)
          case (StructureType2.TypeString, v: String) =>
            ByteArrayHelper.putInt(bb, idx + 3, v.length)
            (0 until v.length).foreach { i =>
              ByteArrayHelper.putChar(bb, idx + 7 + i * 2, v(i))
            }
          case (StructureType2.TypeBigDecimal, v: BigDecimal) =>
            ByteArrayHelper.putInt(bb, idx + 3, v.bigDecimal.scale())
            val ba = v.bigDecimal.unscaledValue.toByteArray
            ByteArrayHelper.putInt(bb, idx + 7, ba.length)
            (0 until ba.length).foreach { i =>
              ByteArrayHelper.put(bb, idx + 11 + i, ba(i))
            }
          case (_@t, _@v) => throw new IllegalArgumentException(s"unsupported combination or type: type `$t` with value `$v`")
        }
        buildByteArray(byteArrayStructure, bb: Array[Byte], idx + 3 + len)
      }
    }

    private def getByteArrayStructure(structureValues: Map[String, Any]) = {
      structureValues.map { case (fieldName, fieldValue) =>
        val fieldId = fieldIdentifiers(fieldName)
        val (typeId, length) = fieldValue match {
          case None => (StructureType2.TypeNone, 0)
          case false => (StructureType2.TypeBooleanFalse, 0)
          case true => (StructureType2.TypeBooleanTrue, 0)
          case v: Long => (StructureType2.TypeLong, 8)
          case v: Double => (StructureType2.TypeDouble, 8)
          case v: TimestampWithOffset => (StructureType2.TypeTimestamp, 8 + 4)
          case v: String =>
            (StructureType2.TypeString, 4 + v.length * 2) // 4 bytes for sizeField + 2 Bytes for every character
          case v: BigDecimal =>
            val size = v.bigDecimal.unscaledValue().bitLength() / 8 + 1
            (StructureType2.TypeBigDecimal, 4 + 4 + size) // 4 bytes for scaleField + 4 bytes for sizeField + 1 Bytes for element in BigDec
          case _@e => throw new IllegalArgumentException(s"Type '${e.getClass.getName}' isn't supported")
        }
        (fieldId, typeId, length, fieldValue)
      }
    }
  }

}