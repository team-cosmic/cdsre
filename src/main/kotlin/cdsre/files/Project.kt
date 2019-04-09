package cdsre.files

import java.io.File

class Project private constructor(val file: File) {

    companion object {
        @JvmStatic
        fun loadProject(file: File) : Project {
            // TODO
            return Project(file)
        }

        @JvmStatic
        fun createProject(filename: String, rom: ROM) : Project {
            val file = File(filename)
            return createProject(file, rom)
        }

        @JvmStatic
        fun createProject(file: File, rom: ROM) : Project {
            // TODO
            return Project(file)
        }
    }

    fun saveProject() {
        // TODO
    }

}