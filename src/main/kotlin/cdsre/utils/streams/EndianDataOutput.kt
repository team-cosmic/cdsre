package cdsre.utils.streams

import cdsre.utils.Endianness
import java.io.Closeable

interface EndianDataOutput : Closeable {

    var endianness: Endianness.Endian

    fun write(out: Int)

    fun write(arr: ByteArray) {
        for(i in 1..arr.size) {
            write(arr[i].toInt())
        }
    }

    fun writeByte(out: Byte)

    fun writeUByte(out: UByte)

    fun writeShort(out: Short)

    fun writeUShort(out: UShort)

    fun writeInt(out: Int)

    fun writeUInt(out: UInt)

    fun writeLong(out: Long)

    fun writeULong(out: ULong)

    fun writeString(out: String)

}