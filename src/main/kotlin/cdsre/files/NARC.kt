package cdsre.files

import com.google.common.io.LittleEndianDataInputStream
import com.google.common.io.LittleEndianDataOutputStream
import java.io.File

@kotlin.ExperimentalUnsignedTypes
fun LittleEndianDataInputStream.readUnsignedInt() = this.readInt().toUInt()

@kotlin.ExperimentalUnsignedTypes
fun LittleEndianDataInputStream.readString(len: UInt): String {
    var out = ""
    for (i in 1u..len) {
        out += this.read().toChar()
    }
    return out
}

@kotlin.ExperimentalUnsignedTypes
fun LittleEndianDataOutputStream.writeUnsignedShort(out: UShort) = this.writeShort(out.toInt())

@kotlin.ExperimentalUnsignedTypes
fun LittleEndianDataOutputStream.writeUnsignedInt(out: UInt) = this.writeInt(out.toInt())

fun LittleEndianDataOutputStream.writeString(str: String) {
    for (char in str) {
        this.writeByte(char.toInt())
    }
}

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
            for (item in filenameTable) {
                out += item.size
            }
            return out
        }

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
            allocationTable = listOf()
            filenameTable = mutableListOf()
            files = mutableListOf()
        } else {
            val dataReader = LittleEndianDataInputStream(file.inputStream())

            val magic = dataReader.readUnsignedInt()
            if (magic != 0x4E415243u && magic != 0x4352414Eu) {
                System.err.println("NARC file $BAD_MAGIC")
            }

            val constant = dataReader.readUnsignedInt()
            if (constant != 0xFEFF0001u && constant != 0x0100FFFEu) {
                System.out.println("Unexpected NARC constant $constant")
            }

            dataReader.skip(4) // Skip file size
            dataReader.skip(2) // Skip header size
            dataReader.skip(2) // Skip number of sections

            allocationTable = readFATB(dataReader)
            filenameTable = readFNTB(dataReader)
            files = readFIMG(dataReader)

            dataReader.close()
        }
    }

    protected fun readFATB(reader: LittleEndianDataInputStream): List<NARCAlloc> {
        val magic = reader.readUnsignedInt()
        if (magic != 0x42544146u && magic != 0x46415442u) {
            System.err.println("Allocation table $BAD_MAGIC")
        }

        val newList: MutableList<NARCAlloc> = ArrayList()

        val size = reader.readUnsignedInt()
        val numFiles = reader.readUnsignedInt()

        assert(size == (12u + numFiles * 8u))

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

        val newList: MutableList<NARCFilename> = ArrayList()

        var pos: UInt = 8u
        while (pos < sectionSize) {
            val startOffset = reader.readUnsignedInt()
            val firstFilePos = reader.readUnsignedShort().toUShort()
            val parentDir = reader.readUnsignedShort().toUShort()
            pos += 8u

            var name: String? = null
            if (pos < sectionSize) {
                val nameSize = reader.readUnsignedByte().toUByte()
                System.out.println(nameSize)
                name = reader.readString(nameSize.toUInt())
                pos += 1u + nameSize
            }

            newList.add(NARCFilename(startOffset, firstFilePos, parentDir, name))
        }

        return newList
    }

    protected fun readFIMG(reader: LittleEndianDataInputStream): MutableList<NARCFile> {
        val magic = reader.readUnsignedInt()
        if (magic != 0x474D4946u && magic != 0x46494D47u) {
            System.err.println("Image Table $BAD_MAGIC")
        }

        // This will need to be changed to support nested directories
        reader.skip(filenameTable[0].startOffset.toLong())

        val newList: MutableList<NARCFile> = ArrayList()

        for (alloc in allocationTable) {
            val size = alloc.end - alloc.start
            val array = ByteArray(size.toInt())
            reader.read(array)
            newList.add(NARCFile(array))
        }

        return newList
    }

    fun save(file: File) {
        val output = LittleEndianDataOutputStream(file.outputStream())

        // Write header
        output.writeString("CRAN")
        output.writeUnsignedInt(0x0100FFFEu)
        output.writeUnsignedInt(fileSize)
        output.writeUnsignedShort(headerSize)
        output.writeUnsignedShort(numSections)

        output.writeString("BTAF")
        output.writeUnsignedInt(fatbSize)
        output.writeUnsignedInt(numFiles)
        var out = 0u
        for (f in files) {
            output.writeUnsignedInt(out)
            out += f.size
            output.writeUnsignedInt(out)
        }

        output.writeString("BTNF")
        output.writeUnsignedInt(fntbSize)
        for (item in filenameTable) {
            output.writeUnsignedInt(item.startOffset)
            output.writeUnsignedShort(item.firstFilePos)
            output.writeUnsignedShort(item.parentDir)
            if (item.name != null) {
                output.writeByte(item.name.length)
                output.writeString(item.name)
            }
        }

        output.writeString("GMIF")
        output.writeUnsignedInt(fimgSize)
        for (f in files) {
            output.write(f.data)
        }

        output.close()
    }

}