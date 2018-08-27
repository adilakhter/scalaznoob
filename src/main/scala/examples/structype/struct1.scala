package examples.structype

import java.nio.ByteBuffer

import scala.annotation.tailrec
import scala.collection.immutable


object struct1 {

  object StructureType {
    val TypeNone: Byte = 0x01
    val TypeBooleanFalse: Byte = 0x02
    val TypeBooleanTrue: Byte = 0x03
    val TypeLong: Byte = 0x04
    val TypeDouble: Byte = 0x05
    val TypeTimestamp: Byte = 0x06
    val TypeString: Byte = 0x07
    val TypeBigDecimal: Byte = 0x08
  }

  case class StructureType(structName: String, fieldIdentifiers: Map[String, Short]) {

    def get(byteArray: Array[Byte], fieldName: String): Any = {
      val lookupFieldId = fieldIdentifiers(fieldName)
      val bb = ByteBuffer.wrap(byteArray)
      bb.mark()
      getFieldValueById(bb, lookupFieldId) match {
        case Some(value) => value
        case None => None
      }
    }

    /**
      * Constructs Map[String, Any] from a ByteArray
      */
    def toMap(byteArray: Array[Byte]): Map[String, Any] = {
      val bb = ByteBuffer.wrap(byteArray)
      bb.mark()

      traverseFields(bb, Seq.empty).toMap
    }

    /**
      *  Creates a byte array from a Map of values.
      *
      * @param fieldvalues
      * @return
      */
    def toByteArray(fieldvalues: Map[String, Any]): Array[Byte] = {
      val byteArrayStructure: Iterable[(Short, Byte, Int, Any)] = getByteArrayStructure(fieldvalues)

      val byteArraySize = byteArrayStructure.map(_._3 + 3).sum

      val byteArray = new Array[Byte](byteArraySize)
      val byteBuffer = ByteBuffer.wrap(byteArray)
      byteBuffer.mark()
      buildByteArray(byteArrayStructure, byteBuffer)
      byteArray
    }

    @tailrec
    private def getFieldValueById(bb: ByteBuffer, lookupFieldId: Short): Option[Any] = {
      if (bb.hasRemaining) {
        val fieldId = bb.getShort()
        val typeId = bb.get()
        if (fieldId == lookupFieldId) {
          Some(getValueByTypeId(bb, typeId))
        } else {
          skipValueByTypeId(bb, typeId)
          getFieldValueById(bb, lookupFieldId)
        }
      } else {
        None
      }
    }

    @tailrec
    private def traverseFields(bb: ByteBuffer, traversedFields: Seq[(String, Any)]): Seq[(String, Any)] = {
      if (bb.hasRemaining) {
        val fieldId = bb.getShort()
        val fieldName = fieldIdentifiers.find { case (_, id) => id == fieldId }.map { case (name, _) => name }.getOrElse(s"unknown-$fieldId")
        val typeId = bb.get()
        val fieldValue: Any = getValueByTypeId(bb, typeId)
        traverseFields(bb, traversedFields :+ fieldName -> fieldValue)
      } else {
        traversedFields
      }
    }

    private def skipValueByTypeId(bb: ByteBuffer, typeId: Byte): Unit = {
      typeId match {
        case StructureType.TypeNone =>
        case StructureType.TypeBooleanFalse =>
        case StructureType.TypeBooleanTrue =>
        case StructureType.TypeLong =>
          val _ = bb.getLong()
        case StructureType.TypeDouble =>
          val _ = bb.getDouble()
        case StructureType.TypeTimestamp =>
          val utc = bb.getLong()
          val _ = bb.getInt()
        case StructureType.TypeString =>
          val size = bb.getInt()/2
          val _ = (0 until size).foreach { idx =>
            bb.getChar
          }
        case StructureType.TypeBigDecimal =>
          val size = bb.getInt()-4
          val scale = bb.getInt()
          val _ = (0 until size).foreach(idx => bb.get)
      }
    }

    private def getValueByTypeId(bb: ByteBuffer, typeId: Byte): Any = {
      val fieldValue = typeId match {
        case StructureType.TypeNone => None
        case StructureType.TypeBooleanFalse => false
        case StructureType.TypeBooleanTrue => true
        case StructureType.TypeLong => bb.getLong()
        case StructureType.TypeDouble => bb.getDouble()
        case StructureType.TypeTimestamp =>
          val utc = bb.getLong()
          val offset = bb.getInt()
          TimestampWithOffset(utc, offset)
        case StructureType.TypeString =>
          val x = new StringBuilder
          val size = bb.getInt()/2
          (0 until size).map { idx =>
            x.append(bb.getChar)
          }
          x.toString()
        case StructureType.TypeBigDecimal =>
          val size = bb.getInt() - 4
          val scale = bb.getInt()
          val arr: Array[Byte] = (0 until size).map(idx => bb.get).toArray
          BigDecimal(BigInt(arr), scale)
      }
      fieldValue
    }

    private def buildByteArray(byteArrayStructure: Iterable[(Short, Byte, Int, Any)], bb: ByteBuffer): Unit = {
      byteArrayStructure.foreach { case (fieldId, typeId, len, fieldValue) =>
        bb.putShort(fieldId)
        bb.put(typeId)
        (typeId, fieldValue) match {
          case (StructureType.TypeNone, _) =>
          case (StructureType.TypeBooleanFalse, false) =>
          case (StructureType.TypeBooleanTrue, true) =>
          case (StructureType.TypeLong, v: Long) =>
            bb.putLong(v)
          case (StructureType.TypeDouble, v: Double) =>
            bb.putDouble(v)
          case (StructureType.TypeTimestamp, v: TimestampWithOffset) =>
            bb.putLong(v.utc)
            bb.putInt(v.offset)
          case (StructureType.TypeString, v: String) =>
            bb.putInt(v.length*2) // size in Char
            v.foreach(bb.putChar)
          case (StructureType.TypeBigDecimal, v: BigDecimal) =>
            val ba = v.bigDecimal.unscaledValue.toByteArray
            bb.putInt(ba.length + 4)
            bb.putInt(v.bigDecimal.scale())
            ba.foreach(bb.put)
          case (_@t, _@v) => throw new IllegalArgumentException(s"unsupported combination or type: type `$t` with value `$v`")
        }
      }
    }

    private def getByteArrayStructure(structureValues: Map[String, Any]): immutable.Iterable[(Short, Byte, Int, Any)] = {
      structureValues.map { case (fieldName, fieldValue) =>
        val fieldId = fieldIdentifiers(fieldName)
        val (typeId, length) = fieldValue match {
          case None => (StructureType.TypeNone, 0)
          case false => (StructureType.TypeBooleanFalse, 0)
          case true => (StructureType.TypeBooleanTrue, 0)
          case v: Long => (StructureType.TypeLong, 8)
          case v: Double => (StructureType.TypeDouble, 8)
          case v: TimestampWithOffset => (StructureType.TypeTimestamp, 8 + 4)
          case v: String =>
            (StructureType.TypeString, 4 + v.length * 2) // 4 bytes for sizeField + 2 Bytes for every character
          case v: BigDecimal =>
            val size = v.bigDecimal.unscaledValue().bitLength() / 8 + 1
            (StructureType.TypeBigDecimal, 4 + 4 + size) // 4 bytes for scaleField + 4 bytes for sizeField + 1 Bytes for element in BigDec
          case _@e => throw new IllegalArgumentException(s"Type '${e.getClass.getName}' isn't supported")
        }
        (fieldId, typeId, length, fieldValue)
      }
    }
  }
}
