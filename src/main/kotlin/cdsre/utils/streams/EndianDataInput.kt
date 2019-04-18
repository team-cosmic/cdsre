package cdsre.utils.streams

import cdsre.utils.Endianness
import java.io.Closeable
import java.io.EOFException
import java.nio.charset.Charset

interface EndianDataInput : Closeable {

	var endianness: Endianness.Endian

	fun read(): Int

	fun read(arr: ByteArray): Int {
		var filled = 0
		for (i in 0 until arr.size) {
			val b = read().toByte()
			if (b < 0)
				return filled
			arr[i] = b
			filled += 1
		}
		return arr.size
	}

	// TODO: Better way, read bytearray then pass to endianness?

	fun readByte(): Byte {
		val byte = read()
		if(byte < 0)
			throw EOFException()
		return byte.toByte()
	}

	fun readUByte(): UByte {
		val byte = read()
		if(byte < 0)
			throw EOFException()
		return byte.toUByte()
	}

	fun readShort(): Short {
		val ch1 = read()
		val ch2 = read()
		if((ch1 or ch2) < 0)
			throw EOFException()
		return Endianness.toShort(ch1, ch2, this.endianness)
	}

	fun readUShort(): UShort {
		val ch1 = read()
		val ch2 = read()

		if((ch1 or ch2) < 0)
			throw EOFException()
		return Endianness.toUShort(ch1, ch2, this.endianness)
	}

	fun readInt(): Int {
		val ch1 = read()
		val ch2 = read()
		val ch3 = read()
		val ch4 = read()

		if((ch1 or ch2 or ch3 or ch4) < 0)
			throw EOFException()
		return Endianness.toInt(ch1, ch2, ch3, ch4, this.endianness)
	}

	fun readUInt(): UInt {
		val ch1 = read()
		val ch2 = read()
		val ch3 = read()
		val ch4 = read()

		if((ch1 or ch2 or ch3 or ch4) < 0)
			throw EOFException()
		return Endianness.toUInt(ch1, ch2, ch3, ch4, this.endianness)
	}

	fun readLong(): Long {
		val ch1 = read()
		val ch2 = read()
		val ch3 = read()
		val ch4 = read()
		val ch5 = read()
		val ch6 = read()
		val ch7 = read()
		val ch8 = read()

		if((ch1 or ch2 or ch3 or ch4 or ch5 or ch6 or ch7 or ch8) < 0)
			throw EOFException()
		return Endianness.toLong(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, this.endianness)
	}

	fun readULong(): ULong {
		val ch1 = read()
		val ch2 = read()
		val ch3 = read()
		val ch4 = read()
		val ch5 = read()
		val ch6 = read()
		val ch7 = read()
		val ch8 = read()

		if((ch1 or ch2 or ch3 or ch4 or ch5 or ch6 or ch7 or ch8) < 0)
			throw EOFException()
		return Endianness.toULong(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, this.endianness)
	}

	fun readString(length: Long): String {
		var out = ""
		for(i in 1..length) {
			out += read().toChar()
		}
		return out
	}

	fun readString(length: ULong): String {
		return readString(length.toLong())
	}

	fun readString(length: Int): String {
		return readString(length.toLong())
	}

	fun readString(length: UInt): String {
		return readString(length.toLong())
	}

	/**
	 * Continuously read from current position until a delimiter string is reached. Optionally case-insensitive
	 * @param end the string marking the end of the read
	 * @param charset which charset to make use of. Assumes UTF-8. Be sure to use {@link kotlin.text.Charsets}
	 * @param ignoreCase {@code true} if case doesn't matter, i.e. is insensitive. Assumes case-sensitive
	 * @return newly-read string, excluding delimiter
	 */
	fun readUntil(end: String, charset: Charset = Charsets.UTF_8, ignoreCase: Boolean = false): String {
		val end = String(end.toByteArray(), charset) //Redefine end to match the provided charset
		var outArray = arrayListOf<Byte>()
		var outString = ""

		//do-while loop guarantees one pass. Ends loop when delimiter is found
		do {

			//Get the next byte, as all charsets need at least one
			outArray.add(read().toChar().toByte())

			if(charset != Charsets.UTF_8 && charset != Charsets.US_ASCII && charset != Charsets.ISO_8859_1) {
				outArray.add(read().toChar().toByte()) //Two bytes for 16-bit

				if(charset == Charsets.UTF_32 || charset == Charsets.UTF_32BE || charset == Charsets.UTF_32LE) {
					outArray.add(read().toChar().toByte()) //Four bytes
					outArray.add(read().toChar().toByte()) //for 32-bit
				}
			}

			outString = String(outArray.toArray() as ByteArray, charset) //Now that we have all the bytes, make a string out of them with the proper charset
		} while(!outString.contains(end, ignoreCase))

		return outString.substring(0, outString.indexOf(end)) //Cut the delimiter off the output string ad return the result
	}
}