package cdsre.files.mapping

open class NarcMapping(val name: String, val functions: List<FunctionDef>, val entries: Map<String, EntryDef>) {
    open fun getEntry(index: Int): Entry {
        return Entry()
    }
}

open class Entry {
    open fun getValue(name: String): ByteArray {
        return ByteArray(0)
    }

    open fun setValue(name: String, data: ByteArray) {

    }
}
