package cdsre.files

import com.google.common.io.LittleEndianDataInputStream
import java.io.File

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

    val filename: String = file?.toString() ?: name

    val fileSize: UInt
    val headerSize: UShort
    val numSections: UShort

    var allocationTable: List<NARCAlloc>
    var filenameTable: List<NARCFilename>
    var files: List<NARCFile>

    init {
        if (file == null) {
            fileSize = 0u
            headerSize = 0u
            numSections = 3u

            allocationTable = listOf()
            filenameTable = listOf()
            files = listOf()
        } else {
            val dataReader = LittleEndianDataInputStream(file.inputStream())
            // Read in offsets
            val magic = dataReader.readUnsignedInt()
            if (magic != 0x4E415243u && magic != 0x4352414Eu) {
                System.err.println("NARC file " + BAD_MAGIC)
            }
            val constant = dataReader.readUnsignedInt()
            System.out.println("Constant: " + magic)

            fileSize = dataReader.readUnsignedInt()
            headerSize = dataReader.readUnsignedShort().toUShort()
            numSections = dataReader.readUnsignedShort().toUShort()

            System.out.println("File Size: " + fileSize)
            System.out.println("Header Size: " + headerSize)
            System.out.println("Number of Sections: " + numSections)

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
            System.err.println("Allocation table " + BAD_MAGIC)
        }

        var newList: MutableList<NARCAlloc> = ArrayList()

        val size = reader.readUnsignedInt()
        System.out.println("Alloc Size: " + size)
        val numFiles = reader.readUnsignedInt()
        System.out.println("Number of files: " + numFiles)

        for (i in 1u..numFiles) {
            val start = reader.readUnsignedInt()
            val end = reader.readUnsignedInt()
            newList.add(NARCAlloc(start, end))
        }

        return newList
    }

    protected fun readFNTB(reader: LittleEndianDataInputStream): List<NARCFilename> {
        val magic = reader.readUnsignedInt()
        if (magic != 0x42544E46u && magic != 0x464E5442u) {
            System.err.println("Filename table " + BAD_MAGIC)
        }

        val sectionSize = reader.readUnsignedInt()
        System.out.println("FNTB Size: " + sectionSize)
        reader.skip(sectionSize.toLong() - 8)
        return listOf()
    }

    protected fun readFIMG(reader: LittleEndianDataInputStream): List<NARCFile> {
        val magic = reader.readUnsignedInt()
        if (magic != 0x474D4946u && magic != 0x46494D47u) {
            System.err.println("Image Table " + BAD_MAGIC)
        }
        reader.skip(4)

        var newList: MutableList<NARCFile> = ArrayList()

        for (alloc in allocationTable) {
            val size = alloc.end - alloc.start
            var array: ByteArray = ByteArray(size.toInt())
            reader.read(array)
            newList.add(NARCFile(size, array))
        }

        return newList
    }

    // TODO: Whatever you put in here

}