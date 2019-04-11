package cdsre.utils

import java.io.EOFException
import java.io.File
import java.io.RandomAccessFile
import cdsre.utils.Endianness.Endian
import cdsre.utils.Endianness.Endian.LITTLE

@kotlin.ExperimentalUnsignedTypes
class EndianRandomAccessFile(file: File, mode: String): RandomAccessFile(file, mode) {

    companion object {
        val DEFAULT_ENDIAN = LITTLE
    }

    val endian: Endian = DEFAULT_ENDIAN

    // Type overrides for unsigned values

    fun seek(loc: UInt) {
        seek(loc.toLong())
    }

    // Methods for reading

    fun readUByte(): UByte {
        val byte = read()
        if(byte < 0)
            throw EOFException()
        return byte.toUByte()
    }

    fun readUShort(): UShort {
        val ch1 = read()
        val ch2 = read()

        if((ch1 or ch2) < 0)
            throw EOFException()
        return Endianness.toUShort(ch1, ch2, this.endian)
    }

    fun readUInt(): UInt {
        val ch1 = read()
        val ch2 = read()
        val ch3 = read()
        val ch4 = read()

        if((ch1 or ch2 or ch3 or ch4) < 0)
            throw EOFException()
        return Endianness.toUInt(ch1, ch2, ch3, ch4, this.endian)
    }
    
    fun readULong(): ULong {
        val ch1 = read()
        val ch2 = read()
        val ch3 = read()
        val ch4 = read()
        val ch5 = read()
        val ch6 = read()
        val ch7 = read()
        val ch8 = read()

        if((ch1 or ch2 or ch3 or ch4 or ch5 or ch6 or ch7 or ch8) < 0)
            throw EOFException()
        return Endianness.toULong(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, this.endian)
    }

    fun readString(length: Long): String {
        var out = ""
        for(i in 1..length) {
            out += read().toChar()
        }
        return out
    }

    // Overrides for other unsigned types

    fun readString(length: ULong): String {
        return readString(length.toLong())
    }

    fun readString(length: UInt): String {
        return readString(length.toLong())
    }

    fun readString(length: UByte): String {
        return readString(length.toLong())
    }

    // Methods for writing

    fun writeUByte(out: UByte) {
        write(out.toInt())
    }

    fun writeUShort(out: UShort) {
        for(byte in Endianness.fromUShort(out, this.endian)) write(byte)
    }

    fun writeUInt(out: UInt) {
        for(byte in Endianness.fromUInt(out, this.endian)) write(byte)
    }

    fun writeULong(out: ULong) {
        for(byte in Endianness.fromULong(out, this.endian)) write(byte)
    }

    fun writeString(out: String) {
        for(char in out) {
            write(char.toInt())
        }
    }
}