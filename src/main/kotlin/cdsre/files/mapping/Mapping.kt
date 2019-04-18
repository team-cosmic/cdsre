package cdsre.files.mapping

import java.io.File

class Mapping private constructor(val narcMappings: Map<String, NarcMapping>) {

    companion object {

        /**
         * Loads a new mapping from a file and returns it
         * @param file: Points to the top level directory of the mapping
         */
        @JvmStatic
        fun loadMapping(file: File): Mapping {
            val filter = MappingFilter()
            val newMaps: MutableMap<String, NarcMapping> = mutableMapOf()
            for (f in file.listFiles()) {
                if (f.extension == "xml") {
                    val map = filter.parse(f)
                    newMaps[map.name] = map
                }
            }
            return Mapping(newMaps)
        }
    }

    val pokemon: PokemonNarcMapping?
        get() = narcMappings["pokemon"] as PokemonNarcMapping

    fun getSupported(): List<String> {
        return listOf()
    }

    fun getMapping(name: String): NarcMapping? {
        return narcMappings[name]
    }

}
