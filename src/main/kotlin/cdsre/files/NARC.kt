package cdsre.files

import cdsre.utils.Constants
import cdsre.utils.EndianRandomAccessFile
import java.io.File

/**
 * Class representing a `.narc` file in memory.
 *
 * @param file: The file being loaded, or null if this NARC is being created from scratch
 * @param name: The name to assign to this narc, if file is null.
 */
@kotlin.ExperimentalUnsignedTypes
class NARC private constructor(file: File?, name: String = "") {

	companion object {

		/**
		 * Loads a NARC from the file system, and returns it
		 *
		 * @param file: File to load data from
		 * @return: New loaded NARC object
		 */
		@JvmStatic
		fun loadNARC(file: File): NARC {
			return NARC(file)
		}
	}

	val filename: String = file?.name ?: "$name.narc"

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
		get() = fatbHeaderSize + files.size.toUInt() * 8u

	val fntbSize: UInt
		get() {
			var out = 8u
			for(item in filenameTable) {
				out += item.size
			}
			return out
		}

	val fimgSize: UInt
		get() {
			var out = 8u
			for(file in files) {
				out += file.size
			}
			return out
		}

	protected var allocationTable: List<NARCAlloc>
	var filenameTable: MutableList<NARCFilename>
	var files: MutableList<NARCFile>

	init {
		if(file == null) {
			allocationTable = listOf()
			filenameTable = mutableListOf()
			files = mutableListOf()
		} else {
			val reader = EndianRandomAccessFile(file, "r")

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
	protected fun readFATB(reader: EndianRandomAccessFile): List<NARCAlloc> {
		val magic = reader.readString(4)
		if(magic != "FATB" && magic != "BTAF") {
			System.err.println("Allocation table ${Constants.BAD_MAGIC}")
		}

		val newList: MutableList<NARCAlloc> = ArrayList()

		val size = reader.readUInt()
		val numFiles = reader.readUInt()

		assert(size == (12u + numFiles * 8u))

		for(i in 1u..numFiles) {
			val start = reader.readUInt()
			val end = reader.readUInt()
			newList.add(NARCAlloc(start, end))
		}

		return newList
	}

	/**
	 * Read the File Name Table from the NARC being loaded
	 *
	 * @param reader: RandomAccess class being used to read the file
	 * @return: List of NARC Filenames objects, defining the filenames and directory
	 * 			structure of the NARC
	 */
	protected fun readFNTB(reader: EndianRandomAccessFile): MutableList<NARCFilename> {
		val magic = reader.readString(4)
		if(magic != "FNTB" && magic != "BTNF") {
			System.err.println("Filename table ${Constants.BAD_MAGIC}")
		}

		val sectionSize = reader.readUInt()

		val newList: MutableList<NARCFilename> = ArrayList()

		var pos: UInt = 8u
		while(pos < sectionSize) {
			val startOffset = reader.readUInt()
			val firstFilePos = reader.readUShort()
			val parentDir = reader.readUShort()
			pos += 8u

			var name: String? = null
			if(pos < sectionSize) {
				val nameSize = reader.readUByte()
				System.out.println(nameSize)
				name = reader.readString(nameSize)
				pos += 1u + nameSize
			}

			newList.add(NARCFilename(startOffset, firstFilePos, parentDir, name))
		}

		return newList
	}

	/**
	 * Read the File Image table from the NARC being loaded
	 *
	 * @param reader: RandomAccess class being used to read the file
	 * @return: List of NARC files, data objects containing the raw file bytes
	 */
	protected fun readFIMG(reader: EndianRandomAccessFile): MutableList<NARCFile> {
		val magic = reader.readString(4)
		if(magic != "FIMG" && magic != "GMIF") {
			System.err.println("Image Table ${Constants.BAD_MAGIC}")
		}

		// This will need to be changed to support nested directories
		reader.seek(reader.filePointer + filenameTable[0].startOffset.toLong())

		val newList: MutableList<NARCFile> = ArrayList()

		for(alloc in allocationTable) {
			val size = alloc.end - alloc.start
			val array = ByteArray(size.toInt())
			reader.read(array)
			newList.add(NARCFile(array))
		}

		return newList
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
		output.writeUInt(fntbSize)
		for(item in filenameTable) {
			output.writeUInt(item.startOffset)
			output.writeUShort(item.firstFilePos)
			output.writeUShort(item.parentDir)
			if(item.name != null) {
				output.writeByte(item.name.length)
				output.writeString(item.name)
			}
		}

		output.writeString("GMIF")
		output.writeUInt(fimgSize)
		for(f in files) {
			output.write(f.data)
		}

		output.close()
	}
}