package cdsre.files

import cdsre.utils.streams.EndianData
import cdsre.utils.streams.EndianRandomAccessFile
import cdsre.utils.streams.VirtualInputStream
import cdsre.utils.streams.VirtualOutputStream
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
 * A single file in a NARC.
 */
@Deprecated("NARC file system handling is changing, this may be replaced")
data class NitroFile(var data: ByteArray) {
    val size: UInt
        get() = data.size.toUInt()
}

/**
 * An abstract representation of a file in a Nitro filetree.
 * many classes within the NitroFS return an instance of this
 */
abstract class RomFile(val path: String) {

    abstract var offset: Long
        protected set

    abstract val realFile: File

    abstract val isVirtual: Boolean

    abstract val isPacked: Boolean

    val components: List<String>

    val filename: String
        get() = components[components.size - 1]

    val exists: Boolean = true

    init {
        val split = path.split("/", "\\")
        components = split
    }

    abstract fun inputStream(): InputStream

    abstract fun outputStream(): OutputStream

    abstract fun randomAccessFile(): EndianData

}

/**
 * A concrete implementation of RomFile. Refers to a single file on
 * the real file system
 *
 * Always counts as being 'unpacked'
 */
class RealRomFile(file: File, path: String) : RomFile(path) {
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
 * A concrete implementation of RomFile. Refers to a single file
 * stored within another file
 *
 * May or may not be 'unpacked'
 */
class VirtualRomFile : RomFile {
    override var offset: Long
    var length: Long
    override val isVirtual: Boolean = true
    override val realFile: File
    override val isPacked: Boolean
        get() = nitroFS.packed

    val nitroFS: NitroFS

    constructor(file: File, alloc: NitroAlloc, path: String, rom: ROM) : super(path) {
        offset = alloc.start.toLong()
        length = (alloc.end - alloc.start).toLong()
        realFile = file
        nitroFS = rom
    }

    constructor(file: RomFile, alloc: NitroAlloc, path: String, narc: NARC) : super(path) {
        offset = alloc.start.toLong() + file.offset
        length = (alloc.end - alloc.start).toLong()
        realFile = file.realFile
        nitroFS = narc
    }

    override fun inputStream(): InputStream {
        if (isPacked) {
            return VirtualInputStream(realFile, offset, length)
        } else {
            TODO("Not implemented")
        }
    }

    override fun outputStream(): OutputStream {
        if (isPacked) {
            return VirtualOutputStream(realFile, offset, length)
        } else {
            TODO("Not implemented")
        }
    }

    override fun randomAccessFile(): EndianData {
        TODO("Not implemented")
    }
}
