package cdsre.utils.streams

import cdsre.files.NitroFS
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class MemoryOutputStream(private val nitroFS: NitroFS, private val id: Int) : OutputStream() {

    val backing = ByteArrayOutputStream()

    override fun write(b: Int) {
        backing.write(b)
    }

    override fun close() {
        backing.close()
        nitroFS.setInMemory(id, backing.toByteArray())
    }

}