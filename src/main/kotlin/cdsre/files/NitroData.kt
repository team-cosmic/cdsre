package cdsre.files

import cdsre.utils.streams.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Class to represent an allocation table entry inside of
 * a nitro archive or nitro rom file system
 */
data class NitroAlloc(val start: UInt, val end: UInt) {
    var name: String? = null
}

/**
 * Abstract class for a Nitro file tree directory
 * Subclassed below by the two specific types of directory
 */
abstract class NitroTree(
    val subtableOffset: UInt,
    val firstFileID: UShort
) {
    val children: MutableList<NitroDir> = mutableListOf()
    val files: MutableList<NitroAlloc> = mutableListOf()

    /**
     * Get the total size of the NitroTree, if it were written to
     * a file.
     */
    open val size: UInt
        get() {
            var out = 8u
            for (item in children) {
                out += item.size
            }
            for (item in files) {
                val len = item.name?.length?.toUInt() ?: 0u
                out += if(len > 0u) len + 1u else 0u
            }
            return out
        }

    override fun toString(): String {
        return "NitroTree(subtableOffset=$subtableOffset, firstFileID=$firstFileID, children=$children, files=$files)"
    }

    /**
     * Attempt to find a sub-file allocation in this tree, from a list
     * of path elements. If a file is unnamed, it can be reached with a path
     * consisting of nothing but its reference number
     *
     * @param file: List of path elements, EG. ["firstdir", "seconddir", "file.name"]
     * @param depth: How deep in the search tree we currently are
     * @return: NitroAlloc instance if it exists, otherwise null
     */
    fun getChild(file: List<String>, depth: Int = 0): NitroAlloc? {
        for (item in children) {
            if (item.name == file[depth]) {
                return item.getChild(file, depth + 1)
            }
        }
        for (item in files) {
            if (item.name == file[depth]) {
                return item
            }
        }

        return null
    }
}

/**
 * The root of a file tree. Has no name, stores the total number of directories
 * and files in the tree
 */
class NitroRoot(
    subtableOffset: UInt,
    firstFileID: UShort,
    val numDirs: UShort
) : NitroTree(subtableOffset, firstFileID)

/**
 * A directory in the tree. Has a name, and references its parent directory
 */
class NitroDir (
    subtableOffset: UInt,
    firstFileID: UShort,
    @Deprecated("This will be auto generated on save")
    val parentDir: UShort,
    var name: String
) : NitroTree(subtableOffset, firstFileID) {
    override val size: UInt
        get() = super.size + name.length.toUInt() + 3u
}

/**
 * An abstract representation of a file in a Nitro filetree.
 * multiple functions within a NitroFS return an instance of this
 */
abstract class NitroFile(val path: String) {

    /**
     * The offset of this file in the real file it is associated with
     */
    abstract var offset: Long
        protected set

    /**
     * The real file in the system this file is associated with
     */
    abstract val realFile: File

    /**
     * If this NitroFile is 'virtual', IE if it is really only a
     * section of an actual file on the device.
     */
    abstract val isVirtual: Boolean

    /**
     * Whether this NitroFile is part of a 'packed' NitroFS or not
     */
    abstract val isPacked: Boolean

    /**
     * A list of the components in the path, EG ["firstdir", "seconddir", "file.name"]
     */
    val components: List<String>

    /**
     * The name of this file, simple the last component in the file path
     */
    val filename: String
        get() = components[components.size - 1]

    /**
     * Whether this file actually exists yet (for adding a new file to
     * an unpacked NitroFS)
     */
    val exists: Boolean = true

    init {
        val split = path.split("/", "\\")
        components = split
    }

    /**
     * Get an InputStream from this file. Follows standard InputStream guarantees
     */
    abstract fun inputStream(): InputStream

    /**
     * Get an OutputStream from this file. In general, changes are not
     * guaranteed to show up in memory until the stream is closed
     */
    abstract fun outputStream(): OutputStream

    /**
     * Get an EndianData reader/writer from this file, which generally
     * follows the RandomAccessFile interface. In general, changes are not
     * guaranteed to show up in memory until the stream is closed
     */
    abstract fun randomAccessFile(): EndianData

}

/**
 * A concrete implementation of NitroFile. Refers to a single file on
 * the real file system
 *
 * Always counts as being 'unpacked'
 */
class RealNitroFile(file: File, path: String) : NitroFile(path) {
    override var offset: Long = 0
    override val isVirtual: Boolean = false
    override val realFile = file
    override val isPacked: Boolean = false

    override fun inputStream(): InputStream {
        return realFile.inputStream()
    }

    override fun outputStream(): OutputStream {
        return realFile.outputStream()
    }

    override fun randomAccessFile(): EndianData {
        return EndianRandomAccessFile(realFile, "rw")
    }
}

/**
 * A concrete implementation of NitroFile. Refers to a single file
 * stored within another file
 *
 * May or may not be 'unpacked'
 */
class VirtualNitroFile : NitroFile {
    override var offset: Long
    var length: Long
    override val isVirtual: Boolean = true
    override val realFile: File
    override val isPacked: Boolean
        get() = nitroFS.packed

    private val alloc: NitroAlloc

    val nitroFS: NitroFS

    constructor(file: File, alloc: NitroAlloc, path: String, rom: ROM) : super(path) {
        this.alloc = alloc
        offset = alloc.start.toLong()
        length = (alloc.end - alloc.start).toLong()
        realFile = file
        nitroFS = rom
    }

    constructor(file: NitroFile, alloc: NitroAlloc, path: String, narc: NARC) : super(path) {
        this.alloc = alloc
        offset = alloc.start.toLong() + file.offset
        length = (alloc.end - alloc.start).toLong()
        realFile = file.realFile
        nitroFS = narc
    }

    override fun inputStream(): InputStream {
        if (isPacked) {
            return VirtualInputStream(realFile, offset, length)
        } else if (nitroFS.inMemory) {
            val id = nitroFS.getIdFromAlloc(alloc)
            return ByteArrayInputStream(nitroFS.getInMemory(id))
        } else {
            throw NotImplementedError("Operation not supported")
        }
    }

    override fun outputStream(): OutputStream {
        if (isPacked) {
            return VirtualOutputStream(realFile, offset, length)
        } else if (nitroFS.inMemory) {
            val id = nitroFS.getIdFromAlloc(alloc)
            return MemoryOutputStream(nitroFS, id)
        } else {
            throw NotImplementedError("Operation not supported")
        }
    }

    override fun randomAccessFile(): EndianData {
        if (isPacked) {
            return VirtualEndianRandomAccessFile(realFile, "rw", offset, length)
        } else if (nitroFS.inMemory) {
            val id = nitroFS.getIdFromAlloc(alloc)
            return MemoryEndianRandomAccess(nitroFS, id, nitroFS.getInMemory(id))
        } else {
            throw NotImplementedError("Operation not supported")
        }
    }
}
