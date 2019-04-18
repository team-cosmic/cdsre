package cdsre.files.mapping

data class FunctionDef(val index: Int, val name: String)

data class VariableDef(val index: Int, val name: String)

data class EntryDef(val name: String, val length: Int, val offset: Int) {

    private val subBack: MutableList<SubentryDef> = mutableListOf()
    val subEntries: List<SubentryDef> = subBack

    fun addSubEntry(subentry: SubentryDef) {
        subBack.add(subentry)
    }

}

data class SubentryDef(val name: String, val length: Int, val offset: Int)