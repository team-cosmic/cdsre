package cdsre.files.mapping

class ScriptNarcMapping(location: String, functions: List<FunctionDef>) : NarcMapping("script", location, functions, mapOf()) {

    override fun getEntry(index: Int): PokemonEntry {
        throw NotImplementedError()
    }

}
