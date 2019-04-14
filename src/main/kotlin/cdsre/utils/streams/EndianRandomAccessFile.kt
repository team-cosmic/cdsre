package cdsre.utils.streams

import cdsre.utils.Endianness
import java.io.EOFException
import java.io.File
import java.io.RandomAccessFile
import cdsre.utils.Endianness.Endian
import cdsre.utils.Endianness.Endian.LITTLE
import javax.naming.OperationNotSupportedException

class EndianRandomAccessFile(file: File, mode: String) : EndianData {

    companion object {
        val DEFAULT_ENDIAN = LITTLE
    }

    private val backing = RandomAccessFile(file, mode)

    override var endianness: Endian = DEFAULT_ENDIAN

    override var filePointer: Long
        get() = backing.filePointer
        set(value) = throw OperationNotSupportedException()

    // EndianData methods

    override fun seek(loc: Long) {
        backing.seek(loc)
    }

    override fun close() {
        backing.close()
    }

    // EndianDataInput methods
    // TODO: Better way, read bytearray then pass to endianness?

    override fun read(): Int {
        return backing.read()
    }

    override fun readByte(): Byte {
        val byte = read()
        if(byte < 0)
            throw EOFException()
        return byte.toByte()
    }

    override fun readUByte(): UByte {
        val byte = read()
        if(byte < 0)
            throw EOFException()
        return byte.toUByte()
    }

    override fun readShort(): Short {
        val ch1 = read()
        val ch2 = read()
        if((ch1 or ch2) < 0)
            throw EOFException()
        return Endianness.toShort(ch1, ch2, this.endianness)
    }

    override fun readUShort(): UShort {
        val ch1 = read()
        val ch2 = read()

        if((ch1 or ch2) < 0)
            throw EOFException()
        return Endianness.toUShort(ch1, ch2, this.endianness)
    }

    override fun readInt(): Int {
        val ch1 = read()
        val ch2 = read()
        val ch3 = read()
        val ch4 = read()

        if((ch1 or ch2 or ch3 or ch4) < 0)
            throw EOFException()
        return Endianness.toInt(ch1, ch2, ch3, ch4, this.endianness)
    }

    override fun readUInt(): UInt {
        val ch1 = read()
        val ch2 = read()
        val ch3 = read()
        val ch4 = read()

        if((ch1 or ch2 or ch3 or ch4) < 0)
            throw EOFException()
        return Endianness.toUInt(ch1, ch2, ch3, ch4, this.endianness)
    }

    override fun readLong(): Long {
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
        return Endianness.toLong(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, this.endianness)
    }
    
    override fun readULong(): ULong {
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
        return Endianness.toULong(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, this.endianness)
    }

   override fun readString(length: Long): String {
        var out = ""
        for(i in 1..length) {
            out += read().toChar()
        }
        return out
    }

    // EndianDataOutput methods

    override fun write(out: Int) {
        backing.write(out)
    }

    override fun writeByte(out: Byte) {
        write(out.toInt())
    }

    override fun writeUByte(out: UByte) {
        write(out.toInt())
    }

    override fun writeShort(out: Short) {
        for(byte in Endianness.fromShort(out, this.endianness)) write(byte)
    }

    override fun writeUShort(out: UShort) {
        for(byte in Endianness.fromUShort(out, this.endianness)) write(byte)
    }

    override fun writeInt(out: Int) {
        for(byte in Endianness.fromInt(out, this.endianness)) write(byte)
    }

    override fun writeUInt(out: UInt) {
        for(byte in Endianness.fromUInt(out, this.endianness)) write(byte)
    }

    override fun writeLong(out: Long) {
        for(byte in Endianness.fromLong(out, this.endianness)) write(byte)
    }

    override fun writeULong(out: ULong) {
        for(byte in Endianness.fromULong(out, this.endianness)) write(byte)
    }

    override fun writeString(out: String) {
        for(char in out) {
            write(char.toInt())
        }
    }
}