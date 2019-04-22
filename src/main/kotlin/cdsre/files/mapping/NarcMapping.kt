package cdsre.files.mapping

import cdsre.workspace.Workspace
import java.io.FileNotFoundException

open class NarcMapping(
    val name: String,
    val location: String,
    val funcDefs: List<FunctionDef>,
    val entryDefs: Map<String, EntryDef>
) {

    protected val cachedEntries: MutableMap<Int, Entry> = mutableMapOf()

    open fun getEntry(index: Int): Entry {
        return cachedEntries[index] ?:
                makeEntry(index)
    }

    open fun makeEntry(index: Int): Entry {
        val rom = Workspace.currentROM ?: throw IllegalStateException()
        val narc = rom.getNarc(location) ?: throw FileNotFoundException()

        val newMap: MutableMap<String, ByteArray> = mutableMapOf()
        val reader = narc.getFile(index).randomAccessFile()

        for ((key, value) in entryDefs) {
            val bytePos = value.offset / 8
            reader.seek(bytePos)
            val byteArray = ByteArray(value.length * (if (value.bits) 8 else 1))
            reader.read(byteArray)

            if (value.bits) {
                newMap[key] = byteArray
            } else {
                val newArr = ByteArray(value.length)
                for (i in 0 until value.length) {
                    newArr[i] = (byteArray[i / 8].toInt() and (0x1 shl (i % 8))).toByte()
                }
                newMap[key] = newArr
            }
        }

        return Entry(this, index, newMap)
    }

    open fun saveEntry(entry: Entry) {
        val rom = Workspace.currentROM ?: throw IllegalStateException()
        val narc = rom.getNarc(location) ?: throw FileNotFoundException()

        // TODO: degenerate map
    }

}

open class Entry internal constructor(
    val mapping: NarcMapping,
    val index: Int,
    private val data: MutableMap<String, ByteArray>
) {
    open fun getValue(name: String): ByteArray? {
        return data[name]
    }

    open fun setValue(name: String, newValue: ByteArray) {
        data[name] = newValue
    }

    open fun save() {
        mapping.saveEntry(this)
    }
}
