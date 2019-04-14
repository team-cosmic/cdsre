package cdsre.utils.streams

import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.io.RandomAccessFile

class VirtualOutputStream(file: File, offset: Long, val length: Long) : OutputStream() {

    private val realOutputFile = RandomAccessFile(file, "rw")
    private var curOffset: Long = 0

    init {
        realOutputFile.seek(offset)
    }

    override fun write(out: Int) {
        if (curOffset >= length)
            throw IOException("Max output length reached")
        curOffset += 1
        realOutputFile.write(out)
    }

    override fun close() {
        super.close()
        realOutputFile.close()
    }

}