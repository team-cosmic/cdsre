package cdsre.files

import cdsre.utils.streams.EndianData
import java.io.FileNotFoundException

/**
 * Abstract class implemented by any file that contains a
 * Nitro file system.
 *
 * A nitro file system is stored in three parts:
 *   A file allocation table (FAT)
 *   A filename table (FNT)
 *   A file data section (FIMG)
 * The allocation table specifies where each file is in the
 * data section, the filename table specifies the directory tree
 * and file names, and the file data section contains the actual files.
 *
 * A NitroFS can be in two states, packed or unpacked. A packed filesystem
 * cannot have its allocation or filename tables changes, and files must
 * stay the same size if altered. An unpacked filesystem does not have these
 * restrictions.
 */
abstract class NitroFS(val packed: Boolean) {

	/**
	 * Holds the actual allocation table for this NitroFS
	 */
	abstract val allocationTable: MutableList<NitroAlloc>

	/**
	 * Holds the actual filename table for this NitroFS
	 */
	abstract val filenameTable: NitroRoot

	/**
	 * Size of the file allocation table for this NitroFS
	 */
	val fatSize: UInt
		get() = allocationTable.size.toUInt() * 8u

	/**
	 * Size of the filename table for this NitroFS
	 */
	val fntSize: UInt
		get() = filenameTable.size

	// Loading functions

	/**
	 * Read the FAT for this object from a file
	 */
	protected fun readFAT(reader: EndianData, fatOffset: UInt, fatSize: UInt): MutableList<NitroAlloc> {
		reader.seek(fatOffset)

		val newList: MutableList<NitroAlloc> = mutableListOf()

		for(i in 1u..(fatSize / 8u)) {
			val start = reader.readUInt()
			val end = reader.readUInt()

			newList.add(NitroAlloc(start, end))
		}

		return newList
	}

	/**
	 * Read the FNT for this object from a file
	 */
	protected fun readFNT(reader: EndianData, fntOffset: UInt): NitroRoot {
		reader.seek(fntOffset)

		val subtableOffset = reader.readUInt()
		val firstFileID = reader.readUShort()
		val numDirs = reader.readUShort()

		val newRoot = NitroRoot(subtableOffset, firstFileID, numDirs)

		readSubtable(reader, fntOffset, newRoot)

		for (alloc in allocationTable) {
			if (alloc.name != null)
				continue

			newRoot.files.add(alloc)
		}

		return newRoot
	}

	/**
	 * Recursive call for readFNT that reads a directory subtable, and all of its directories
	 * and their subtables, etc.
	 */
	protected fun readSubtable(reader: EndianData, fntOffset: UInt, dir: NitroTree) {
		reader.seek(fntOffset + dir.subtableOffset)
		var typeLen = reader.readUByte().toUInt()
		var fileID: Int = dir.firstFileID.toInt()

		while(true) {
			if(typeLen == 0u) {
				return
			}
			val name = reader.readString(typeLen and 0x7Fu)
			if(typeLen in 0x01u..0x7Fu) {
				val alloc = allocationTable[fileID]
				alloc.name = name
				dir.files.add(alloc)
				fileID += 1
			} else if(typeLen in 0x81u..0xFFu) {
				val id = reader.readUShort() and 0xFFFu
				val curPos = reader.filePointer

				reader.seek(fntOffset + (id * 8u))
				val subtableOffset = reader.readUInt()
				val firstFileID = reader.readUShort()
				val parentDir = reader.readUShort() and 0xFFFu

				val newDir = NitroDir(subtableOffset, firstFileID, parentDir, name)
				readSubtable(reader, fntOffset, newDir)
				reader.seek(curPos)

				dir.children.add(newDir)
			}

			typeLen = reader.readUByte().toUInt()
		}
	}

	// Get a file

	/**
	 * Indicates whether a nitroFS implements the 'in-memory files' interface, meaning that
	 * it reads its virtual files into an internal memory representation.
	 *
	 * If it implements this interface, it may be unpacked despite being virtual
	 */
	open val inMemory: Boolean
		get() = false

	/**
	 * Convert a NitroAlloc object into its corresponding in-memory ID
	 *
	 * @param alloc: NitroAlloc to get ID of
	 * @return: Id of the allocation, or -1 in the case of alloc not existing
	 */
	open fun getIdFromAlloc(alloc: NitroAlloc): Int {
		throw NotImplementedError()
	}

	/**
	 * Get and return the ByteArray for an in-memory file, from its ID
	 *
	 * @param id: ID of the file to get
	 * @return: ByteArray containing the file's data
	 */
	open fun getInMemory(id: Int): ByteArray {
		throw NotImplementedError()
	}

	/**
	 * Set the ByteArray for an in-memory file, from its ID
	 *
	 * @param id: ID of the file to set
	 * @param arr: ByteArray containing the data to put in the file.
	 */
	open fun setInMemory(id: Int, arr: ByteArray) {
		throw NotImplementedError()
	}

	/**
	 * Gets a NitroFile from within this filesystem. This is a sub-file
	 * within the larger file that is the current NitroFS object.
	 *
	 * @param path: Path to the desired file, as a string. EG "dirone/dirtwo/file.name"
	 * @return: NitroFile pointing to the file if it exists
	 * @throws FileNotFoundException: If the file path doesn't exist
	 */
	abstract fun getFile(path: String): NitroFile

}
