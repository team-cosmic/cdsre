package cdsre.files

import cdsre.utils.Constants
import cdsre.utils.streams.EndianData
import java.io.File
import java.io.FileNotFoundException

/**
 * Class representing a `.narc` file in memory.
 *
 * @param file: The file being loaded. If it doesn't exist, this NARC is being created
 *              from scratch.
 */
class NARC private constructor(protected val file: NitroFile) : NitroFS(file.isVirtual) {

	companion object {

		/**
		 * Loads a NARC from the file system, and returns it
		 *
		 * @param file: File to load data from
		 * @return: New loaded NARC object
		 */
		@JvmStatic
		fun loadNARC(file: File): NARC {
            val romFile = RealNitroFile(file, file.path)
			return NARC(romFile)
		}

		/**
		 * Loads a NARC from inside of a ROM and returns it
		 *
		 * @param file: File to load from
		 * @return: New loaded NARC object
		 */
		@JvmStatic
		fun loadNARC(file: NitroFile): NARC {
			return NARC(file)
		}
	}

	/**
	 * The total size of this NARC in memory, if it were written to a file
	 */
	val fileSize: UInt
		get() {
			return headerSize + fatbSize + fntbSize + fimgSize
		}

	/**
	 * The size of the NARC header
	 */
	val headerSize: UShort
		get() = 16u

	/**
	 * The number of sections in the NARC
	 */
	val numSections: UShort
		get() = 3u

	/**
	 * The number of files in this NARC
	 */
	val numFiles: UInt
		get() = memoryFiles.size.toUInt()

	/**
	 * The size of the FATB header
	 */
	val fatbHeaderSize: UInt
		get() = 12u

	/**
	 * The total size of the FATB section
	 */
	val fatbSize: UInt
		get() = fatbHeaderSize + fatSize

	/**
	 * The size of the FNTB header
	 */
	val fntbHeaderSize: UInt
		get() = 8u

	/**
	 * The total size of the FNTB section
	 */
	val fntbSize: UInt
		get() = fntbHeaderSize + fntSize

	/**
	 * The total size of the FIMG section
	 */
	val fimgSize: UInt
		get() {
			var out = 8u
			for(file in memoryFiles) {
				out += file.size.toUInt()
			}
			return out
		}

	override val allocationTable: MutableList<NitroAlloc>
	override val filenameTable: NitroRoot

	/**
	 * Holds the in-memory files for the NitroFS in-memory interface
	 */
    private val memoryFiles: MutableList<ByteArray>

	init {
		if(!file.exists) {
			allocationTable = mutableListOf()
			filenameTable = NitroRoot(4u, 0u, 1u)
			memoryFiles = mutableListOf()
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
			memoryFiles = readFIMG(reader)

			reader.close()
		}
	}

	/**
	 * Read the FATB section of the NARC being loaded.
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
	 * Read the FNTB section of the NARC being loaded
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
	 * Read the FIMG section of the NARC being loaded
	 *
	 * @param reader: RandomAccess class being used to read the file
	 * @return: List of NARC files, data objects containing the raw file bytes
	 */
	protected fun readFIMG(reader: EndianData): MutableList<ByteArray> {
		val magic = reader.readString(4)
		if(magic != "FIMG" && magic != "GMIF") {
			System.err.println("Image Table ${Constants.BAD_MAGIC}")
		}

		val length = reader.readUInt()

		val newList: MutableList<ByteArray> = ArrayList()

		for(alloc in allocationTable) {
			val size = alloc.end - alloc.start
			val array = ByteArray(size.toInt())
			reader.read(array)
			newList.add(array)
		}

		return newList
	}

	/**
	 * NARC files do support the in-memory interface
	 */
    override val inMemory: Boolean
        get() = true

	/**
	 * Converts a NitroAlloc to its ID into the in-memory files
	 */
    override fun getIdFromAlloc(alloc: NitroAlloc): Int {
        for (id in 0 until allocationTable.size) {
            if (allocationTable[id] == alloc) {
                return id
            }
        }
        return -1
    }

	/**
	 * Return the bytearray of an in-memory file
	 *
	 * @param id: The id of the file to get
	 * @return: A ByteArray containing the data in the given file
	 */
    override fun getInMemory(id: Int): ByteArray {
        return memoryFiles[id]
    }

	/**
	 * Set the byte array of an in-memory file
	 *
	 * @param id: The id of the file to set
	 * @param arr: The ByteArray to set the file to
	 */
    override fun setInMemory(id: Int, arr: ByteArray) {
        memoryFiles[id] = arr
		// TODO: Refresh allocs when this is done
    }

	/**
	 * Get a NitroFile pointing to the desired file path. For a NARC,
	 * this will always be a VirtualNitroFile instance
	 *
	 * @param path: Path of the desired file
	 */
	override fun getFile(path: String): NitroFile {
		val alloc = this.filenameTable.getChild(path.split("/", "\\")) ?: throw FileNotFoundException()
		return VirtualNitroFile(this.file, alloc, path, this)
	}

	/**
	 * Save this NARC to a new file
	 *
	 * @param file: File location to save this NARC to. It will be created if it doesn't
	 * 				exist, or overwritten if it already does.
	 */
	fun save(file: File) {
		TODO("not implemented")
		/*val output = EndianRandomAccessFile(file, "rw")

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

		output.close()*/
	}
}