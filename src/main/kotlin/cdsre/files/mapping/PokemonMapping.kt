package cdsre.files.mapping

import cdsre.files.mapping.Entry
import cdsre.files.mapping.NarcMapping

class PokemonNarcMapping(entries: Map<String, EntryDef>) : NarcMapping("pokemon", listOf(), entries) {
    val cachedEntries: List<Entry> = listOf()

    override fun getEntry(index: Int): Entry {
        return cachedEntries[index]
    }
}

class PokemonEntry : Entry() {

    // Whatever properties

    override fun getValue(name: String): ByteArray {
        return ByteArray(0)
    }

    override fun setValue(name: String, data: ByteArray) {}

    fun getName() {}

    fun getAbility1() {}
}
