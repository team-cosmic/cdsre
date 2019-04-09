package cdsre.files

import java.io.File

class ROM private constructor(val file: File) {

    companion object {

        @JvmStatic
        fun loadROM(file: File) : ROM {
            return ROM(file)
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