package cdsre.utils.streams

import cdsre.files.NitroFS
import cdsre.utils.Constants
import cdsre.utils.Endianness

class MemoryEndianRandomAccess(private val nitroFS: NitroFS, private val id: Int, arr: ByteArray) : EndianData {

    override var endianness: Endianness.Endian = Constants.DEFAULT_ENDIAN

    override var filePointer: Long = 0

    private var backingArray = arr.copyOf()

    override fun read(): Int {
        val out = backingArray[filePointer.toInt()].toInt()
        filePointer += 1
        return out
    }

    override fun write(out: Int) {
        if (filePointer > backingArray.size) {
            val newArr = ByteArray(filePointer.toInt())
            backingArray = backingArray.copyInto(newArr)
        }
        backingArray[filePointer.toInt()] = out.toByte()
        filePointer += 1
    }

    override fun seek(loc: Long) {
        filePointer = loc
    }

    override fun close() {
        nitroFS.setInMemory(id, backingArray)
    }

}