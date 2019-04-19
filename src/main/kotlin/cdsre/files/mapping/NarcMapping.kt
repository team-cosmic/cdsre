package cdsre.files.mapping

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
        return Entry()
    }

}

open class Entry internal constructor() {
    open fun getValue(name: String): ByteArray {
        return ByteArray(0)
    }

    open fun setValue(name: String, data: ByteArray) {

    }
}
