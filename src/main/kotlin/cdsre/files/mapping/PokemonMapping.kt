package cdsre.files.mapping

import kotlin.reflect.KClass

class PokemonNarcMapping(location: String, entries: Map<String, EntryDef>) : NarcMapping("pokemon", location, listOf(), entries) {

    override fun getEntry(index: Int): PokemonEntry {
        return super.getEntry(index) as PokemonEntry
    }

    override fun getEntryType(): KClass<out Entry> {
        return PokemonEntry::class
    }

}

class PokemonEntry(mapping: PokemonNarcMapping, index: Int, data: MutableMap<String, ByteArray>) : Entry(mapping, index, data) {

    // Whatever properties

    fun getName(): String {
        TODO()
    }

    fun getHP(): Int {
        val arr = getValue("HP") ?: return 0
        return arr[0].toInt()
    }
}
