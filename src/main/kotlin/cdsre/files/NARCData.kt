package cdsre.files

@kotlin.ExperimentalUnsignedTypes
data class NARCAlloc(val start: UInt, val end: UInt)

@kotlin.ExperimentalUnsignedTypes
data class NARCFilename(
    val startOffset: UInt,
    val firstFilePos: UShort,
    val parentDir: UShort,
    val name: String?
    ) {

    val size: UInt
        get() = 8u + (if (name == null) 0u else name.length.toUInt() + 1u)
}

@kotlin.ExperimentalUnsignedTypes
data class NARCFile(var data: ByteArray) {
    val size: UInt
        get() = data.size.toUInt()
}
