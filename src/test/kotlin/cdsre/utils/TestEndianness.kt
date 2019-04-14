package cdsre.utils

import kotlin.test.Test
import cdsre.utils.Endianness.Endian


class TestEndianness {
    @Test
    fun testToUShort() {
        val b1 = 0x12
        val b2 = 0x34

        assert(Endianness.toUShort(b1, b2, Endian.LITTLE) == 0x3412.toUShort())
        assert(Endianness.toUShort(b1, b2, Endian.BIG) == 0x1234.toUShort())

        assert(Endianness.toUShort(b2, b1, Endian.BIG) == 0x3412.toUShort())
        assert(Endianness.toUShort(b2, b1, Endian.LITTLE) == 0x1234.toUShort())
    }

    @Test
    fun testToUInt() {
        val b1 = 0x12
        val b2 = 0x34
        val b3 = 0x56
        val b4 = 0x78

        assert(Endianness.toUInt(b1, b2, b3, b4, Endian.LITTLE) == 0x78563412u)
        assert(Endianness.toUInt(b1, b2, b3, b4, Endian.BIG) == 0x12345678u)

        assert(Endianness.toUInt(b4, b3, b2, b1, Endian.BIG) == 0x78563412u)
        assert(Endianness.toUInt(b4, b3, b2, b1, Endian.LITTLE) == 0x12345678u)
    }

    @Test
    fun testToULong() {
        val b1 = 0x12
        val b2 = 0x34
        val b3 = 0x56
        val b4 = 0x78
        val b5 = 0x9A
        val b6 = 0xBC
        val b7 = 0xDE
        val b8 = 0xF0

        assert(Endianness.toULong(b1, b2, b3, b4, b5, b6, b7, b8, Endian.LITTLE) == 0xF0DEBC9A78563412u)
        assert(Endianness.toULong(b1, b2, b3, b4, b5, b6, b7, b8, Endian.BIG) == 0x123456789ABCDEF0u)
    }

    @Test
    fun testToShort() {
        val b1 = 0x12
        val b2 = 0x34

        assert(Endianness.toShort(b1, b2, Endian.LITTLE) == 0x3412.toShort())
        assert(Endianness.toShort(b1, b2, Endian.BIG) == 0x1234.toShort())

        assert(Endianness.toShort(b2, b1, Endian.BIG) == 0x3412.toShort())
        assert(Endianness.toShort(b2, b1, Endian.LITTLE) == 0x1234.toShort())
    }

    @Test
    fun testToLong() {
        val b1 = 0x12
        val b2 = 0x34
        val b3 = 0x56
        val b4 = 0x78
        val b5 = 0x9A
        val b6 = 0xBC
        val b7 = 0xDE
        val b8 = 0xF0

        // assert(Endianness.toLong(b1, b2, b3, b4, b5, b6, b7, b8, Endian.LITTLE) == 0xF0DEBC9A78563412) Not possible
        // assert(Endianness.toLong(b8, b7, b6, b5, b4, b3, b2, b1, Endian.BIG) == 0xF0DEBC9A78563412) Not possible

        // assert(Endianness.toLong(b1, b2, b3, b4, b5, b6, b7, b8, Endian.LITTLE) == 0x123456789ABCDEF0) Errors

        assert(Endianness.toLong(b1, b2, b3, b4, b5, b6, b7, b8, Endian.BIG) == 0x123456789ABCDEF0) // Okay
        assert(Endianness.toLong(b8, b7, b6, b5, b4, b3, b2, b1, Endian.LITTLE) == 0x123456789ABCDEF0) // Okay

    }
}