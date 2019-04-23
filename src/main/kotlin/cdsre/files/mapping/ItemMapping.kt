package cdsre.files.mapping

import kotlin.reflect.KClass

class ItemNarcMapping(location: String, entries: Map<String, EntryDef>) : NarcMapping("item", location, listOf(), entries) {

    override fun getEntry(index: Int): ItemEntry {
        return super.getEntry(index) as ItemEntry
    }

    override fun getEntryType(): KClass<out Entry> {
        return ItemEntry::class
    }

}

class ItemEntry(mapping: ItemNarcMapping, index: Int, data: MutableMap<String, ByteArray>) : Entry(mapping, index, data) {

    fun getSomething() {
        TODO()
    }

}
