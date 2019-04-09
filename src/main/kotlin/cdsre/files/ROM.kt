package cdsre.files

class ROM private constructor(val filename: String) {

    companion object {

        @JvmStatic
        fun loadROM(filename: String) {
            ROM(filename)
        }

    }

    // TODO: Properties
    val narcs: MutableList<NARC> = loadNarcs()

    init {
        // TODO: load ROM
    }

    private fun loadNarcs(): MutableList<NARC> {
        // TODO: Load NARC files
        return mutableListOf()
    }

    fun saveROM() {
        // TODO: Save ROM
    }

}