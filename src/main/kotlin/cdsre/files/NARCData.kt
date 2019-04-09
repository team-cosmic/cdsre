package cdsre.files

data class NARCAlloc(val start: UInt, val end: UInt)

data class NARCFilename(
    val startOffset: UInt,
    val firstFilePos: UShort,
    val parentDir: UShort
    )

data class NARCFile(var data: ByteArray) {
    val size: UInt
        get() = data.size.toUInt()
}
