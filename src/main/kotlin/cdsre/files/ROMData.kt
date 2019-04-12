package cdsre.files

data class ARMConfig(
    val offset: UInt,
    val entryAddress: UInt,
    val ramAddress: UInt,
    val size: UInt,
    val overlayOffset: UInt,
    val overlaySize: UInt,
    val autoloadListHookRAMAddress: UInt
)

data class IconTitle(
    val version: UShort,
    val crc1: UShort,
    val crc2: UShort,
    val crc3: UShort,
    val crc4: UShort,
    val bitmap: ByteArray,
    val palette: ByteArray,
    val titleJapanese: String,
    val titleEnglish: String,
    val titleFrench: String,
    val titleGerman: String,
    val titleItalian: String,
    val titleSpanish: String,
    val titleChinese: String,
    val titleKorean: String
)

data class ROMFilenames(
    var name: String
)
