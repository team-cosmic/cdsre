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
        /*val int = out.toInt()
        if(endian == Endian.LITTLE) {
            write((int shr 0) and 0xFF)
            write((int shr 8) and 0xFF)
        } else {
            write((int shr 8) and 0xFF)
            write((int shr 0) and 0xFF)
        }*/
    }

    fun writeUInt(out: UInt) {
        for(byte in Endianness.fromUInt(out, this.endian)) write(byte)
        /*val int = out.toInt()
        if(endian == Endian.LITTLE) {
            write((int shr 0) and 0xFF)
            write((int shr 8) and 0xFF)
            write((int shr 16) and 0xFF)
            write((int shr 24) and 0xFF)
        } else {
            write((int shr 24) and 0xFF)
            write((int shr 16) and 0xFF)
            write((int shr 8) and 0xFF)
            write((int shr 0) and 0xFF)
        }*/
    }

    fun writeString(out: String) {
        for(char in out) {
            write(char.toInt())
        }
    }
}