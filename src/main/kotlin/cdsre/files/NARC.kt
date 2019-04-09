package cdsre.files

import com.google.common.io.LittleEndianDataInputStream
import java.io.File

@kotlin.ExperimentalUnsignedTypes
fun LittleEndianDataInputStream.readUnsignedInt() = this.readInt().toUInt()

@kotlin.ExperimentalUnsignedTypes
class NARC private constructor(file: File?, name: String = "") {

    companion object {

        @JvmStatic
        fun loadNARC(file: File): NARC {
            return NARC(file)
        }

    }

    private val BAD_MAGIC: String = "magic does not match expected value. File corrupted?"

    val filename: String = file?.name ?: "$name.narc"

    val fileSize: UInt // TODO: Make this a getter based on files and stuff

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
        get() = throw NotImplementedError()

    val fimgSize: UInt
        get() {
            var out = 8u
            for (file in files) {
                out += file.size
            }
            return out
        }

    protected var allocationTable: List<NARCAlloc>
    var filenameTable: MutableList<NARCFilename>
    var files: MutableList<NARCFile>

    init {
        if (file == null) {
            fileSize = 0u

            allocationTable = listOf()
            filenameTable = mutableListOf()
            files = mutableListOf()
        } else {
            val dataReader = LittleEndianDataInputStream(file.inputStream())
            // Read in offsets
            val magic = dataReader.readUnsignedInt()
            if (magic != 0x4E415243u && magic != 0x4352414Eu) {
                System.err.println("NARC file $BAD_MAGIC")
            }
            val constant = dataReader.readUnsignedInt()
            System.out.println("Constant: $magic")

            fileSize = dataReader.readUnsignedInt()
            dataReader.skip(2) // Skip header size
            dataReader.skip(2) // Skip number of sections

            System.out.println("File Size: $fileSize")

            allocationTable = readFATB(dataReader)
            filenameTable = readFNTB(dataReader)
            files = readFIMG(dataReader)

            System.out.println(allocationTable)
            System.out.println(filenameTable)
            System.out.println(files)
        }
    }

    protected fun readFATB(reader: LittleEndianDataInputStream): List<NARCAlloc> {
        val magic = reader.readUnsignedInt()
        if (magic != 0x42544146u && magic != 0x46415442u) {
            System.err.println("Allocation table $BAD_MAGIC")
        }

        val newList: MutableList<NARCAlloc> = ArrayList()

        val size = reader.readUnsignedInt()
        System.out.println("Alloc Size: $size")
        val numFiles = reader.readUnsignedInt()
        System.out.println("Number of files: $numFiles")

        for (i in 1u..numFiles) {
            val start = reader.readUnsignedInt()
            val end = reader.readUnsignedInt()
            newList.add(NARCAlloc(start, end))
        }

        return newList
    }

    protected fun readFNTB(reader: LittleEndianDataInputStream): MutableList<NARCFilename> {
        val magic = reader.readUnsignedInt()
        if (magic != 0x42544E46u && magic != 0x464E5442u) {
            System.err.println("Filename table $BAD_MAGIC")
        }

        val sectionSize = reader.readUnsignedInt()
        System.out.println("FNTB Size: $sectionSize")
        reader.skip(sectionSize.toLong() - 8)
        return mutableListOf()
    }

    protected fun readFIMG(reader: LittleEndianDataInputStream): MutableList<NARCFile> {
        val magic = reader.readUnsignedInt()
        if (magic != 0x474D4946u && magic != 0x46494D47u) {
            System.err.println("Image Table $BAD_MAGIC")
        }
        reader.skip(4)

        val newList: MutableList<NARCFile> = ArrayList()

        for (alloc in allocationTable) {
            val size = alloc.end - alloc.start
            val array = ByteArray(size.toInt())
            reader.read(array)
            newList.add(NARCFile(array))
        }

        return newList
    }

}