package cdsre.files.mapping

import cdsre.utils.Constants
import java.io.File
import java.nio.file.Path

class Mapping private constructor(val narcMappings: Map<String, NarcMapping>) {

    companion object {

        /**
         * Finds and loads a mapping from CDSRE's default search path
         * @param name: Name of the mapping to search for
         */
        @JvmStatic
        fun getMapping(name: String): Mapping? {
            val searchLoc = File(Path.of(Constants.CDSRE_PATH, "mappings").toString())
            for (f in searchLoc.listFiles()) {
                if (f.isDirectory && f.name == name) {
                    return loadMapping(f)
                }
            }
            return null
        }

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
