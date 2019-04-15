package cdsre.files

import cdsre.utils.streams.EndianData

/**
 * Abstract class implemented by any file that contains a
 * Nitro file system.
 *
 * A nitro file system is stored in three parts:
 *   A file allocation table
 *   A filename table
 *   A file data section
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

	abstract val allocationTable: MutableList<NitroAlloc>
	abstract val filenameTable: NitroRoot

	val fatSize: UInt
		get() = allocationTable.size.toUInt() * 8u

	val fntSize: UInt
		get() = filenameTable.size

	// Loading functions

	fun readFAT(reader: EndianData, fatOffset: UInt, fatSize: UInt): MutableList<NitroAlloc> {
		reader.seek(fatOffset)

		val newList: MutableList<NitroAlloc> = mutableListOf()

		for(i in 1u..(fatSize / 8u)) {
			val start = reader.readUInt()
			val end = reader.readUInt()

			newList.add(NitroAlloc(start, end))
		}

		return newList
	}

	fun readFNT(reader: EndianData, fntOffset: UInt): NitroRoot {
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

	fun readSubtable(reader: EndianData, fntOffset: UInt, dir: NitroTree) {
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
	 * Returns -1 in the case of alloc not existing
	 */
	open fun getIdFromAlloc(alloc: NitroAlloc): Int {
		throw NotImplementedError()
	}

	open fun getInMemory(id: Int): ByteArray {
		throw NotImplementedError()
	}

	open fun setInMemory(id: Int, arr: ByteArray) {
		throw NotImplementedError()
	}

	abstract fun getFile(path: String): NitroFile

	// Debug stuff

	fun recursePrint(dir: NitroRoot) {
		println("root")
		for (child in dir.children) {
			_print(child, 1)
		}
		var count = 0
		for (file in dir.files) {
			println(file.name ?: count)
			count += 1
		}
	}

	fun _print(dir: NitroDir, depth: Int) {
		println(" ".repeat(depth) + dir.name)
		for (child in dir.children) {
			_print(child, depth + 1)
		}
		for (file in dir.files) {
			println(" ".repeat(depth + 1) + file.name)
		}
	}
}
