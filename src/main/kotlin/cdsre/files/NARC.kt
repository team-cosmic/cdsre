package cdsre.files

import cdsre.utils.Constants
import cdsre.utils.streams.EndianData
import cdsre.utils.streams.EndianRandomAccessFile
import java.io.File
import java.io.FileNotFoundException

/**
 * Class representing a `.narc` file in memory.
 *
 * @param file: The file being loaded, or null if this NARC is being created from scratch
 */
class NARC private constructor(protected val file: RomFile) : NitroFS(file.isVirtual) {

	companion object {

		/**
		 * Loads a NARC from the file system, and returns it
		 *
		 * @param file: File to load data from
		 * @return: New loaded NARC object
		 */
		@JvmStatic
		fun loadNARC(file: File): NARC {
            val romFile = RealRomFile(file, file.path)
			return NARC(romFile)
		}

		/**
		 * Loads a NARC from inside of a ROM and returns it
		 *
		 * @param file: File to load from
		 * @return: New loaded NARC object
		 */
		@JvmStatic
		fun loadNARC(file: RomFile): NARC {
			return NARC(file)
		}
	}

	val fileSize: UInt
		get() {
			return headerSize + fatbSize + fntbSize + fimgSize
		}

	val headerSize: UShort
		get() = 16u

	val numSections: UShort
		get() = 3u

	val numFiles: UInt
		get() = files.size.toUInt()

	val fatbHeaderSize: UInt
		get() = 12u

	val fatbSize: UInt
		get() = fatbHeaderSize + fatSize

	val fntbHeaderSize: UInt
		get() = 8u

	val fntbSize: UInt
		get() = fntbHeaderSize + fntSize

	val fimgSize: UInt
		get() {
			var out = 8u
			for(file in files) {
				out += file.size
			}
			return out
		}

	override val allocationTable: MutableList<NitroAlloc>
	override val filenameTable: NitroRoot
	var files: MutableList<NitroFile>

	init {
		if(!file.exists) {
			allocationTable = mutableListOf()
			filenameTable = NitroRoot(4u, 0u, 1u)
			files = mutableListOf()
		} else {
			val reader = file.randomAccessFile()

			val magic = reader.readString(4)
			if(magic != "NARC" && magic != "CRAN") {
				System.err.println("NARC file ${Constants.BAD_MAGIC}")
			}

			val constant = reader.readUInt()
			if(constant != 0xFEFF0001u && constant != 0x0100FFFEu) {
				System.out.println("Unexpected NARC constant $constant")
			}

			// Skip file size (4), header size (2), number of sections (2)
			reader.seek(reader.filePointer + 8)

			allocationTable = readFATB(reader)
			filenameTable = readFNTB(reader)
			files = readFIMG(reader)

			reader.close()
		}
	}

	/**
	 * Read the File Allocation Table from the NARC being loaded.
	 *
	 * @param reader: RandomAccess class being used to read the file
	 * @return: List of NARC allocations
	 */
	protected fun readFATB(reader: EndianData): MutableList<NitroAlloc> {
		val magic = reader.readString(4)
		if(magic != "FATB" && magic != "BTAF") {
			System.err.println("Allocation table ${Constants.BAD_MAGIC}")
		}

		val size = reader.readUInt()
		val numFiles = reader.readUInt()

		assert(size == (12u + numFiles * 8u))

		return readFAT(reader, reader.filePointer.toUInt(), size - 12u)
	}

	/**
	 * Read the File Name Table from the NARC being loaded
	 *
	 * @param reader: RandomAccess class being used to read the file
	 * @return: List of NARC Filenames objects, defining the filenames and directory
	 * 			structure of the NARC
	 */
	protected fun readFNTB(reader: EndianData): NitroRoot {
		val magic = reader.readString(4)
		if(magic != "FNTB" && magic != "BTNF") {
			System.err.println("Filename table ${Constants.BAD_MAGIC}")
		}

		val sectionSize = reader.readUInt()
		val startPointer = reader.filePointer
		val result = readFNT(reader, startPointer.toUInt())
		reader.seek(startPointer + sectionSize.toLong() - 8)

		return result
	}

	/**
	 * Read the File Image table from the NARC being loaded
	 *
	 * @param reader: RandomAccess class being used to read the file
	 * @return: List of NARC files, data objects containing the raw file bytes
	 */
	protected fun readFIMG(reader: EndianData): MutableList<NitroFile> {
		val magic = reader.readString(4)
		if(magic != "FIMG" && magic != "GMIF") {
			System.err.println("Image Table ${Constants.BAD_MAGIC}")
		}

		val length = reader.readUInt()

		val newList: MutableList<NitroFile> = ArrayList()

		for(alloc in allocationTable) {
			val size = alloc.end - alloc.start
			val array = ByteArray(size.toInt())
			reader.read(array)
			newList.add(NitroFile(array))
		}

		return newList
	}

	override fun getFile(path: String): RomFile {
		val alloc = this.filenameTable.getChild(path.split("/", "\\")) ?: throw FileNotFoundException()
		return VirtualRomFile(this.file, alloc, path, this)
	}

	/**
	 * Save this NARC to a new file
	 *
	 * @param file: File location to save this NARC to. It will be created if it doesn't
	 * 				exist, or overwritten if it already does.
	 */
	fun save(file: File) {
		val output = EndianRandomAccessFile(file, "rw")

		// Write header
		output.writeString("NARC")
		output.writeUInt(0x0100FFFEu)
		output.writeUInt(fileSize)
		output.writeUShort(headerSize)
		output.writeUShort(numSections)

		output.writeString("BTAF")
		output.writeUInt(fatbSize)
		output.writeUInt(numFiles)
		var out = 0u
		for(f in files) {
			output.writeUInt(out)
			out += f.size
			output.writeUInt(out)
		}

		output.writeString("BTNF")
		output.writeUInt(fntSize + 8u)
		/*for(item in filenameTable) {
			output.writeUInt(item.startOffset)
			output.writeUShort(item.firstFileID)
			output.writeUShort(item.parentDir)
			if(item.name != null) {
				output.writeByte(item.name.length)
				output.writeString(item.name)
			}
		}*/

		output.writeString("GMIF")
		output.writeUInt(fimgSize)
		for(f in files) {
			output.write(f.data)
		}

		output.close()
	}
}