package cdsre.files.mapping

class PokemonNarcMapping(location: String, entries: Map<String, EntryDef>) : NarcMapping("pokemon", location, listOf(), entries) {

    override fun getEntry(index: Int): PokemonEntry {
        return super.getEntry(index) as PokemonEntry
    }

    override fun makeEntry(index: Int): PokemonEntry {
        return PokemonEntry()
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
