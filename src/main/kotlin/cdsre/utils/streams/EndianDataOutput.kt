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

    fun writeByte(out: Byte) {
        write(out.toInt())
    }

    fun writeUByte(out: UByte) {
        write(out.toInt())
    }

    fun writeShort(out: Short) {
        for(byte in Endianness.fromShort(out, this.endianness)) write(byte)
    }

    fun writeUShort(out: UShort) {
        for(byte in Endianness.fromUShort(out, this.endianness)) write(byte)
    }

    fun writeInt(out: Int) {
        for(byte in Endianness.fromInt(out, this.endianness)) write(byte)
    }

    fun writeUInt(out: UInt) {
        for(byte in Endianness.fromUInt(out, this.endianness)) write(byte)
    }

    fun writeLong(out: Long) {
        for(byte in Endianness.fromLong(out, this.endianness)) write(byte)
    }

    fun writeULong(out: ULong) {
        for(byte in Endianness.fromULong(out, this.endianness)) write(byte)
    }

    fun writeString(out: String) {
        for(char in out) {
            write(char.toInt())
        }
    }

}