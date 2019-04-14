package cdsre.utils

class Endianness {
    enum class Endian {
        LITTLE, BIG;
    }
    
    companion object {
        /**
         * @param b1 first byte
         * @param b2 second byte
         * @return a Short packed with the bytes in {@link Endian#LITTLE}
         */
        fun toShort(b1: Byte, b2: Byte): Short {
            return toUShort(b1, b2).toShort()
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @return a Short packed with the bytes in {@link Endian#LITTLE}
         */
        fun toShort(b1: Int, b2: Int): Short {
            return toUShort(b1, b2, Endian.LITTLE).toShort()
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @param endianness which endian to pack bytes with
         * @return a Short packed with the bytes
         */
        fun toShort(b1: Int, b2: Int, endianness: Endian): Short {
            return toUShort(b1, b2, endianness).toShort()
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return a Short packed with the bytes
         */
        fun toShort(bytes: IntArray, endianness: Endian): UShort {
            var short: UInt = (0.toUInt())
            if(endianness == Endian.LITTLE)
                for(i in 1 downTo 0) short += (((bytes[i] and 0xFF) shl (8*i)).toUInt())
            else
                for(i in 0..1) short += (((bytes[i] and 0xFF) shl (8*(1-i))).toUInt())
            return short.toUShort()
        }
        
        
        /**
         * @param b1 first byte
         * @param b2 second byte
         * @return a UShort packed with the bytes in {@link Endian#LITTLE}
         */
        fun toUShort(b1: Byte, b2: Byte): UShort {
            return toUShort(b1.toInt(), b2.toInt(), Endian.LITTLE)
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @return a UShort packed with the bytes in {@link Endian#LITTLE}
         */
        fun toUShort(b1: Int, b2: Int): UShort {
            return toUShort(b1, b2, Endian.LITTLE)
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @param endianness which endian to pack bytes with
         * @return a UShort packed with the bytes
         */
        fun toUShort(b1: Int, b2: Int, endianness: Endian): UShort {
            return toUShort(intArrayOf(b1, b2), endianness)
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return a UShort packed with the bytes
         */
        fun toUShort(bytes: IntArray, endianness: Endian): UShort {
            var short: Int = 0
            if(endianness == Endian.LITTLE)
                for(i in 1 downTo 0) short += ((bytes[i] and 0xFF) shl (8*i))
            else
                for(i in 0..1) short += ((bytes[i] and 0xFF) shl (8*(1-i)))
            return short.toUShort()
        }
        
        
        
        
        /**
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @return an Int packed with the bytes in {@link Endian#LITTLE}
         */
        fun toInt(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Int {
            return  toUInt(b1, b2, b3, b4).toInt()
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @return an Int packed with the bytes in {@link Endian#LITTLE}
         */
        fun toInt(b1: Int, b2: Int, b3: Int, b4: Int): Int {
            return toUInt(b1, b2, b3, b4, Endian.LITTLE).toInt()
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @param endianness which endian to pack bytes with
         * @return an Int packed with the bytes
         */
        fun toInt(b1: Int, b2: Int, b3: Int, b4: Int, endianness: Endian): Int {
            return toUInt(b1, b2, b3, b4, endianness).toInt()
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return an Int packed with the bytes
         */
        fun toInt(bytes: IntArray, endianness: Endian): Int {
            return toUInt(bytes, endianness).toInt()
        }
        
        
        /**
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @return a UInt packed with the bytes in {@link Endian#LITTLE}
         */
        fun toUInt(b1: Byte, b2: Byte, b3: Byte, b4: Byte): UInt {
            return  toUInt(b1.toInt(), b2.toInt(), b3.toInt(), b4.toInt())
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @return a UInt packed with the bytes in {@link Endian#LITTLE}
         */
        fun toUInt(b1: Int, b2: Int, b3: Int, b4: Int): UInt {
            return toUInt(b1, b2, b3, b4, Endian.LITTLE)
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @param endianness which endian to pack bytes with
         * @return a UInt packed with the bytes
         */
        fun toUInt(b1: Int, b2: Int, b3: Int, b4: Int, endianness: Endian): UInt {
            return toUInt(intArrayOf(b1, b2, b3, b4), endianness)
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return a UInt packed with the bytes
         */
        fun toUInt(bytes: IntArray, endianness: Endian): UInt {
            var int: Int = 0
            if(endianness == Endian.LITTLE)
                for(i in 3 downTo 0) int += ((bytes[i] and 0xFF) shl (8*i))
            else
                for(i in 0..3) int += ((bytes[i] and 0xFF) shl (8*(3-i)))
            return int.toUInt()
        }
        
        
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @param b5 fifth byte
         * @param b6 sixth byte
         * @param b7 seventh byte
         * @param b8 eigth byte
         * @param endianness which endian to pack bytes with
         * @return a Long packed with the bytes
         */
        fun toLong(b1: Int, b2: Int, b3: Int, b4: Int, b5: Int, b6: Int, b7: Int, b8: Int, endianness: Endian): Long {
            return toULong(intArrayOf(b1, b2, b3, b4, b5, b6, b7, b8), endianness).toLong()
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return a Long packed with the bytes
         */
        fun toLong(bytes: IntArray, endianness: Endian): Long {
            return toULong(bytes, endianness).toLong()
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @param b5 fifth byte
         * @param b6 sixth byte
         * @param b7 seventh byte
         * @param b8 eigth byte
         * @param endianness which endian to pack bytes with
         * @return a ULong packed with the bytes
         */
        fun toULong(b1: Int, b2: Int, b3: Int, b4: Int, b5: Int, b6: Int, b7: Int, b8: Int, endianness: Endian): ULong {
            return toULong(intArrayOf(b1, b2, b3, b4, b5, b6, b7, b8), endianness)
        }
        
        
        /**
         * Uses the least significant byte of each input
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return a ULong packed with the bytes
         */
        fun toULong(bytes: IntArray, endianness: Endian): ULong {
            var long: Long = 0
            if(endianness == Endian.LITTLE)
                for(i in 7 downTo 0) long += ((bytes[i] and 0xFF).toLong() shl (8*i))
            else
                for(i in 0..7) long += ((bytes[i] and 0xFF).toLong() shl (8*(7-i)))
            return long.toULong()
        }
        
        
        
        
        /**
         * @param short signed short to unpack
         * @param endianness which endian to unpack bytes by
         * @return an array containing the bytes ordered by endianness
         */
        fun fromShort(short: Short, endianness: Endian): IntArray {
            val int = short.toInt()
            return toIntArray(int, 2, endianness)
        }
        
        
        /**
         * @param ushort unsigned short to unpack
         * @param endianness which endian to unpack bytes by
         * @return an array containing the bytes ordered by endianness
         */
        fun fromUShort(ushort: UShort, endianness: Endian): IntArray {
            val int = ushort.toInt()
            return toIntArray(int, 2, endianness)
        }
        
        
        /**
         * @param int signed int to unpack
         * @param endianness which endian to unpack bytes by
         * @return an array containing the bytes ordered by endianness
         */
        fun fromInt(int: Int, endianness: Endian): IntArray {
            return toIntArray(int, 4, endianness)
        }
        
        
        /**
         * @param uint unsigned int to unpack
         * @param endianness which endian to unpack bytes by
         * @return an array containing the bytes ordered by endianness
         */
        fun fromUInt(uint: UInt, endianness: Endian): IntArray {
            val int = uint.toInt()
            return toIntArray(int, 4, endianness)
        }
        
        
        /**
         * @param long signed long to unpack
         * @param endianness which endian to unpack bytes by
         * @return an array containing the bytes ordered by endianness
         */
        fun fromLong(long: Long, endianness: Endian): IntArray {
            return longToIntArray(long, endianness)
        }
        
        
        /**
         * @param ulong unsigned long to unpack
         * @param endianness which endian to unpack bytes by
         * @return an array containing the bytes ordered by endianness
         */
        fun fromULong(ulong: ULong, endianness: Endian): IntArray {
            val long = ulong.toLong()
            return longToIntArray(long, endianness)
        }
        
        
        
        
        /**
         * Unpack any value of size int or smaller into an array
         * @param int the value to unpack into an array
         * @param endianness the endianness by which to unpack
         * @param byteCount the number of bytes to unpack
         * @return the array of the bytes ordered by endianness
         */
        private fun toIntArray(int: Int, byteCount: Int, endianness: Endian): IntArray {
            if(endianness == Endian.LITTLE) {
                return IntArray(byteCount){ i -> ((int shr (i*8)) and 0xFF).toInt() }
            } else {
                return IntArray(byteCount){ i -> ((int shr ((byteCount-1-i)*8)) and 0xFF).toInt() }
            }
        }
        
        
        /**
         * Long version of {@link toIntArray}
         * @param long the value to unpack into an array
         * @param endianness the endianness by which to unpack
         * @return the array of the bytes ordered by endianness
         */
        private fun longToIntArray(long: Long, endianness: Endian): IntArray {
            if(endianness == Endian.LITTLE) {
                return IntArray(8){ i -> ((long shr (    i*8)) and 0xFF).toInt() }
            } else {
                return IntArray(8){ i -> ((long shr ((7-i)*8)) and 0xFF).toInt() }
            }
            
        }
        
        
        
        
    }
    
    
    
    
}
