package cdsre.files

data class NARCAlloc(val start: UInt, val end: UInt)

data class NARCFilename(
    val startOffset: UInt,
    val firstFilePos: UShort,
    val parentDir: UShort
    )

data class NARCFile(
    val size: UInt,
    val data: ByteArray
)
