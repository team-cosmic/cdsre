package cdsre.utils

import java.nio.file.Path

object Constants {
	const val BAD_MAGIC = "magic does not match expected value. File corrupted?"
	const val CURRENT_VERSION = "0.0.1a"

	val DEFAULT_ENDIAN = Endianness.Endian.LITTLE

	val CDSRE_PATH: String = if (System.getProperty("os.name").indexOf("Windows") >= 0)
		Path.of(System.getenv("LocalAppData"),  "CDSRE").toString() else
		Path.of(System.getProperty("user.home"), ".local", "share", "CDSRE").toString()
}
