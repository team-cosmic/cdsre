package cdsre.files

import java.io.File


open class NarcMapping {
    fun getEntry(index: Int): Entry {
        return Entry()
    }
}

open class Entry {
    fun getValue(name: String): ByteArray {
        return ByteArray(0)
    }

    fun setValue(name: String, data: ByteArray) {

    }
}

class Mapping private constructor(file: File) {

    companion object {

        /**
         * Loads a new mapping from a file and returns it
         * @param file: Points to the top level directory of the mapping
         */
        @JvmStatic
        fun loadMapping(file: File): Mapping {
            return Mapping(file)
        }
    }

    protected val narcMappings: Map<String, NarcMapping> = mapOf()

    val pokemon: PokemonNarcMapping?
        get() = narcMappings["pokemon"] as PokemonNarcMapping

    fun getSupported(): List<String> {
        return listOf()
    }

    fun getMapping(name: String): NarcMapping? {
        return narcMappings[name]
    }

}