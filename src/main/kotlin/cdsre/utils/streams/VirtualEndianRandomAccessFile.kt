package cdsre.utils.streams

import cdsre.utils.Constants
import cdsre.utils.Endianness
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import javax.naming.OperationNotSupportedException

class VirtualEndianRandomAccessFile(file: File, mode: String, private val offset: Long, val length: Long) : EndianData {

    private val backing = RandomAccessFile(file, mode)

    private var curOffset: Long = 0

    override var endianness: Endianness.Endian = Constants.DEFAULT_ENDIAN

    override var filePointer: Long
        get() = backing.filePointer - offset
        set(value) = throw OperationNotSupportedException()

    init {
        backing.seek(offset)
    }

    override fun read(): Int {
        if (curOffset >= length)
            return -1
        curOffset += 1
        return backing.read()
    }

    override fun write(out: Int) {
        if (curOffset >= length)
            throw IOException("Max output length reached")
        curOffset += 1
        backing.write(out)
    }

    override fun seek(loc: Long) {
        if (loc >= length)
            throw IOException("Cannot seek beyond output length")
        backing.seek(offset + loc)
    }

    override fun close() {
        backing.close()
    }

}