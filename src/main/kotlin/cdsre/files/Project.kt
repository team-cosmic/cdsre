package cdsre.files

import cdsre.utils.EndianRandomAccessFile
import java.io.File

@kotlin.ExperimentalUnsignedTypes
class Project private constructor(file: File, var name: String = "CDSRE Project") {

    companion object {

        @JvmStatic
        fun loadProject(file: File) : Project {
            // TODO
            return Project(file)
        }

        @JvmStatic
        fun createProject(filename: String, name: String) : Project {
            val file = File(filename)
            return createProject(file, name)
        }

        @JvmStatic
        fun createProject(file: File, name: String) : Project {
            return Project(file, name)
        }

    }

    private val BAD_MAGIC: String = "magic does not match expected value. File corrupted?"

    var version: String = "0.0.1a"

    var home: String = "./"
    var root: String = "root/"

    init {
        if (!file.exists()) {
            file.createNewFile()
        } else {
            val reader = EndianRandomAccessFile(file, "r")

            val magic = reader.readString(8u)
            if (magic != "CsmcDSRE" && magic != "ERSDcmsC") {
                System.out.println("Cosmic ROM Project " + BAD_MAGIC)
            }

            val nameLength = reader.readInt()

        }
    }

    fun save() {
        // TODO
    }

}