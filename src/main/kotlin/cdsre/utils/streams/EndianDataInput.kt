package cdsre.utils.streams

import cdsre.utils.Endianness
import java.io.Closeable

interface EndianDataInput : Closeable {

    var endianness: Endianness.Endian

    fun read(): Int

    fun read(arr: ByteArray): Int {
        var filled = 0
        for (i in 1..arr.size) {
            val b = read().toByte()
            if (b < 0)
                return filled
            arr[i] = b
            filled += 1
        }
        return arr.size
    }

    fun readByte(): Byte

    fun readUByte(): UByte

    fun readShort(): Short

    fun readUShort(): UShort

    fun readInt(): Int

    fun readUInt(): UInt

    fun readLong(): Long

    fun readULong(): ULong

    fun readString(length: Long): String

    fun readString(length: ULong): String {
        return readString(length.toLong())
    }

    fun readString(length: Int): String {
        return readString(length.toLong())
    }

    fun readString(length: UInt): String {
        return readString(length.toLong())
    }

}