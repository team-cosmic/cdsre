package cdsre.files

import cdsre.utils.streams.EndianRandomAccessFile
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

/**
 * Class representing a '.nds' file, or the equivalent unpacked
 * root directory.
 *
 * @param file: The file being loaded. Could be either a file or a
 * 				directory. If it doesn't exist, this ROM is being
 * 				created from scratch.
 */
class ROM private constructor(file: File) : NitroFS(file.isFile) {

	companion object {
		@JvmStatic
		fun loadROM(file: File): ROM {
			return ROM(file)
		}
	}

	protected val narcCache: MutableMap<String, NARC> = mutableMapOf()
	protected val path: String = file.path

	var gameTitle: String
	var gameCode: String
	var makerCode: String
	val unitCode: UByte
	val encryptionSeedSelect: UByte
	val deviceCapacity: UByte  // TODO: maybe make this equal to 128kb shl value when set?

	val ndsRegion: UByte
	val romVersion: UByte
	val autostart: UByte

	val arm9Config: ARMConfig
	val arm7Config: ARMConfig

	val fntOffset: UInt

	override val allocationTable: MutableList<NitroAlloc>
	override val filenameTable: NitroRoot

	val fatOffset: UInt

	val portSettingsNormal: UInt
	val portSettingsKey1: UInt

	val iconTitleOffset: UInt
	val iconTitle: IconTitle

	val secureAreaChecksum: UShort
	val secureAreaDelay: UShort
	val secureAreaDisable: ULong

	val totalUsedSize: UInt

	val headerSize: UInt
		get() = 0x4000u

	val nintendoLogo: ByteArray
	val logoChecksum: UShort
		get() = 0xCF56u
	val headerChecksum: UShort

	val debugOffset: UInt
	val debugSize: UInt
	val debugRamAddress: UInt


	init {
		if (file.isFile) {
			// TODO: Load .nds
			val reader = EndianRandomAccessFile(file, "r")

			gameTitle = reader.readString(12)
			gameCode = reader.readString(4)
			makerCode = reader.readString(2)

			unitCode = reader.readUByte()
			encryptionSeedSelect = reader.readUByte()
			deviceCapacity = reader.readUByte()

			// Skip over reserved data
			reader.seek(reader.filePointer + 8)

			ndsRegion = reader.readUByte()
			romVersion = reader.readUByte()
			autostart = reader.readUByte()

			// Beginning of ARM sections
			val offset9 = reader.readUInt()
			val entryAddress9 = reader.readUInt()
			val ramAddress9 = reader.readUInt()
			val size9 = reader.readUInt()

			val offset7 = reader.readUInt()
			val entryAddress7 = reader.readUInt()
			val ramAddress7 = reader.readUInt()
			val size7 = reader.readUInt()

			fntOffset = reader.readUInt()
			// Skip over FNT size
			reader.seek(reader.filePointer + 4)

			fatOffset = reader.readUInt()
			val fatSize = reader.readUInt()

			val overlayOffset9 = reader.readUInt()
			val overlaySize9 = reader.readUInt()
			val overlayOffset7 = reader.readUInt()
			val overlaySize7 = reader.readUInt()

			portSettingsNormal = reader.readUInt()
			portSettingsKey1 = reader.readUInt()

			iconTitleOffset = reader.readUInt()
			secureAreaChecksum = reader.readUShort()
			secureAreaDelay = reader.readUShort()

			val autoloadListHookRAMAddress9 = reader.readUInt()
			val autoloadListHookRAMAddress7 = reader.readUInt()

			secureAreaDisable = reader.readULong()
			totalUsedSize = reader.readUInt()

			// Skip over header size
			reader.seek(reader.filePointer + 0x4)

			// Skip over more reserved memory
			reader.seek(reader.filePointer + 0x38)

			nintendoLogo = ByteArray(0x9C)
			reader.read(nintendoLogo)

			// Skip over logo checksum
			reader.seek(reader.filePointer + 2)

			headerChecksum = reader.readUShort()

			debugOffset = reader.readUInt()
			debugSize = reader.readUInt()
			debugRamAddress = reader.readUInt()

			arm9Config = ARMConfig(
				offset9, entryAddress9, ramAddress9, size9, overlayOffset9, overlaySize9, autoloadListHookRAMAddress9
			)
			arm7Config = ARMConfig(
				offset7, entryAddress7, ramAddress7, size7, overlayOffset7, overlaySize7, autoloadListHookRAMAddress7
			)

			iconTitle = readIconTitle(reader)

			allocationTable = readFAT(reader, fatOffset, fatSize)
			filenameTable = readFNT(reader, fntOffset)

		} else if (file.isDirectory) {
			TODO("not implemented")
			gameTitle = ""
			gameCode = ""
			makerCode = ""
			unitCode = 0u
			encryptionSeedSelect = 0u
			deviceCapacity = 0u
			ndsRegion = 0u
			romVersion = 0u
			autostart = 0u

			arm9Config = ARMConfig(
				0u, 0u, 0u, 0u, 0u, 0u, 0u
			)
			arm7Config = ARMConfig(
				0u, 0u, 0u, 0u, 0u, 0u, 0u
			)

			fntOffset = 0u
			filenameTable = NitroRoot(4u, 0u, 1u)
			fatOffset = 0u
			allocationTable = mutableListOf()
			portSettingsNormal = 0u
			portSettingsKey1 = 0u
			iconTitleOffset = 0u
			secureAreaChecksum = 0u
			secureAreaDelay = 0u
			secureAreaDisable = 0u
			totalUsedSize = 0u
			nintendoLogo = ByteArray(0)
			headerChecksum = 0u
			debugOffset = 0u
			debugSize = 0u
			debugRamAddress = 0u

			iconTitle = IconTitle(0u, 0u, 0u, 0u, 0u, ByteArray(0), ByteArray(0),
				"", "", "", "", "", "", "",
				"")
		} else {
			throw FileNotFoundException()
		}
	}

	protected fun readIconTitle(reader: EndianRandomAccessFile): IconTitle {
		reader.seek(this.iconTitleOffset)

		val version = reader.readUShort()
		val crc1 = reader.readUShort()
		val crc2 = reader.readUShort()
		val crc3 = reader.readUShort()
		val crc4 = reader.readUShort()

		// Skip reserved space
		reader.seek(reader.filePointer + 0x16)

		val bitmap = ByteArray(0x200)
		reader.read(bitmap)
		val palette = ByteArray(0x20)
		reader.read(palette)

		val titleJapanese = reader.readString(128)
		val titleEnglish = reader.readString(128)
		val titleFrench = reader.readString(128)
		val titleGerman = reader.readString(128)
		val titleItalian = reader.readString(128)
		val titleSpanish = reader.readString(128)
		var titleChinese = ""
		var titleKorean = ""
		if (version >= 2u) {
			titleChinese = reader.readString(128)
		}
		if (version >= 3u) {
			titleKorean = reader.readString(128)
		}

		return IconTitle(version, crc1, crc2, crc3, crc4, bitmap, palette, titleJapanese, titleEnglish, titleFrench,
			titleGerman, titleItalian, titleSpanish, titleChinese, titleKorean)
	}

	// Internal load functions

	private fun loadNarc(path: String): NARC {
		if(narcCache[path] != null) {
			return narcCache[path] ?: throw ConcurrentModificationException()
		} else {
			val romFile = getFile(path)
			val narc = NARC.loadNARC(romFile)
			narcCache[path] = narc
			return NARC.loadNARC(romFile)
		}
	}

	// Public API

	fun getNarc(name: String): NARC? {
		return try {
			loadNarc(name)
		} catch (e: FileNotFoundException) {
			null
		}
	}

	override fun getFile(path: String): NitroFile {
		if(!packed) {
			val file = Paths.get(this.path, path).toFile()
			if (!file.exists())
				throw FileNotFoundException()
			return RealNitroFile(Paths.get(this.path, path).toFile(), path)
		} else {
			val alloc = this.filenameTable.getChild(path.split("\\", "/")) ?: throw FileNotFoundException()
			return VirtualNitroFile(File(this.path), alloc, path, this)
		}
	}

	fun save() {
		TODO("not implemented")
	}
}