package cdsre.utils.streams

import java.io.File
import java.io.InputStream

class VirtualInputStream(file: File, offset: Long, val length: Long) : InputStream() {

    private val realInputStream: InputStream = file.inputStream()
    private var curOffset: Long = 0

    init {
        realInputStream.skip(offset)
    }

    override fun read(): Int {
        if (curOffset >= length)
            return -1
        curOffset += 1
        return realInputStream.read()
    }

    override fun close() {
        super.close()
        realInputStream.close()
    }

}