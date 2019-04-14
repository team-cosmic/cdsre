package cdsre.utils.streams

import cdsre.utils.Constants
import java.io.File
import java.io.RandomAccessFile
import cdsre.utils.Endianness.Endian
import javax.naming.OperationNotSupportedException

class EndianRandomAccessFile(file: File, mode: String) : EndianData {

    private val backing = RandomAccessFile(file, mode)

    override var endianness: Endian = Constants.DEFAULT_ENDIAN

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

    override fun read(): Int {
        return backing.read()
    }

    override fun write(out: Int) {
        backing.write(out)
    }
}