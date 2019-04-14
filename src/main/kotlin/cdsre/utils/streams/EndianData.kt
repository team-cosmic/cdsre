package cdsre.utils.streams

interface EndianData : EndianDataInput, EndianDataOutput {

    var filePointer: Long

    fun seek(loc: Long)

    fun seek(loc: ULong) {
        seek(loc.toLong())
    }

    fun seek(loc: Int) {
        seek(loc.toLong())
    }

    fun seek(loc: UInt) {
        seek(loc.toLong())
    }

}