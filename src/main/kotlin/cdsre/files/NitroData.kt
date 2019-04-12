package cdsre.files

import cdsre.utils.EndianRandomAccessFile
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream

data class NitroAlloc(val start: UInt, val end: UInt) {
    var name: String? = null
}

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

class NitroRoot(
    subtableOffset: UInt,
    firstFileID: UShort,
    val numDirs: UShort
) : NitroTree(subtableOffset, firstFileID)

class NitroDir (
    subtableOffset: UInt,
    firstFileID: UShort,
    val parentDir: UShort,
    var name: String
) : NitroTree(subtableOffset, firstFileID) {
    override val size: UInt
        get() = super.size + name.length.toUInt() + 3u
}

data class NitroFile(var data: ByteArray) {
    val size: UInt
        get() = data.size.toUInt()
}

abstract class RomFile(val path: String) {

    abstract var offset: UInt
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

    abstract fun randomAccessFile(): EndianRandomAccessFile

}

// Represents a file on the 'real' file system
class RealRomFile(file: File, path: String) : RomFile(path) {
    override var offset = 0u
    override val isVirtual: Boolean = false
    override val realFile = file
    override val isPacked: Boolean = false

    override fun inputStream(): InputStream {
        return realFile.inputStream()
    }

    override fun outputStream(): OutputStream {
        return realFile.outputStream()
    }

    override fun randomAccessFile(): EndianRandomAccessFile {
        return EndianRandomAccessFile(realFile, "rw")
    }
}

// Represents a file that only exists inside a virtual file system. May or may not be packed
class VirtualRomFile : RomFile {
    override var offset: UInt
    override val isVirtual: Boolean = true
    override val realFile: File
    override val isPacked: Boolean
        get() = nitroFS.packed

    val nitroFS: NitroFS

    constructor(file: File, alloc: NitroAlloc, path: String, rom: ROM) : super(path) {
        offset = alloc.start
        realFile = file
        nitroFS = rom
    }

    constructor(file: RomFile, alloc: NitroAlloc, path: String, narc: NARC) : super(path) {
        offset = alloc.start + file.offset
        realFile = file.realFile
        nitroFS = narc
    }

    override fun inputStream(): InputStream {
        TODO("Not implemented")
    }

    override fun outputStream(): OutputStream {
        TODO("Not implemented")
    }

    override fun randomAccessFile(): EndianRandomAccessFile {
        TODO("Not implemented")
    }
}
