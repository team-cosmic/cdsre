package cdsre.files

class Project private constructor(val filename: String) {

    companion object {
        @JvmStatic
        fun loadProject(filename: String) {
            // TODO
        }

        @JvmStatic
        fun createProject(filename: String, rom: ROM) {
            // TODO
        }
    }

    fun saveProject() {
        // TODO
    }

}