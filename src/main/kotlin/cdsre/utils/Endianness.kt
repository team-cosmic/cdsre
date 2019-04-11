package cdsre.utils

@kotlin.ExperimentalUnsignedTypes
class Endianness {
    enum class Endian {
        LITTLE, BIG;
    }
    
    companion object {
        /**
         * Note: Does not check if the inputs are actually bytes
         * @param b1 first byte
         * @param b2 second byte
         * @param endianness which endian to pack bytes with
         * @return a UShort packed with the bytes
         */
        public fun toUShort(b1: Int, b2: Int, endianness: Endian): UShort{
            return toUShort(intArrayOf(b1, b2), endianness)
        }
        
        
        /**
         * Note: Does not check if the inputs are actually bytes
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return a UShort packed with the bytes
         */
        public fun toUShort(bytes: IntArray, endianness: Endian): UShort{
            var short: Int = 0
            if(endianness == Endian.LITTLE)
                for(i in 1 downTo 0) short += (bytes[i] shl (8*i))
            else
                for(i in 0..1) short += (bytes[i] shl (8*i))
            return short.toUShort()
        }
        
        /**
         * Note: Does not check if the inputs are actually bytes
         * @param b1 first byte
         * @param b2 second byte
         * @param b3 third byte
         * @param b4 fourth byte
         * @param endianness which endian to pack bytes with
         * @return a UInt packed with the bytes
         */
        public fun toUInt(b1: Int, b2: Int, b3: Int, b4: Int, endianness: Endian): UInt{
            return toUInt(intArrayOf(b1, b2, b3, b4), endianness)
        }
        
        /**
         * Note: Does not check if the inputs are actually bytes
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return a UInt packed with the bytes
         */
        public fun toUInt(bytes: IntArray, endianness: Endian): UInt{
            var int: Int = 0
            if(endianness == Endian.LITTLE)
                for(i in 3 downTo 0) int += (bytes[i] shl (8*i))
            else
                for(i in 0..3) int += (bytes[i] shl (8*i))
            return int.toUInt()
        }
        
        /**
         * Note: Does not check if the inputs are actually bytes
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
        public fun toULong(b1: Int, b2: Int, b3: Int, b4: Int, b5: Int, b6: Int, b7: Int, b8: Int, endianness: Endian): ULong{
            return toULong(intArrayOf(b1, b2, b3, b4, b5, b6, b7, b8), endianness)
        }
        
        /**
         * Note: Does not check if the inputs are actually bytes
         * @param bytes an array containing the bytes
         * @param endianness which endian to pack bytes with
         * @return a ULong packed with the bytes
         */
        public fun toULong(bytes: IntArray, endianness: Endian): ULong{
            var long: Long = 0
            if(endianness == Endian.LITTLE)
            	for(i in 7 downTo 0) long += (bytes[i].toLong() shl (8*i))
            else
                for(i in 0..7) long += (bytes[i].toLong() shl (8*i))
            return long.toULong()
        }
        
        /**
         * @param ushort unsigned short to unpack
         * @param endianness which endian to unpack bytes by
         * @param an array containing the bytes ordered by endianness
         */
        public fun fromUShort(ushort: UShort, endianness: Endian): IntArray{
            val int = ushort.toInt()
            return toIntArray(int, 2, endianness)
        }
        
        /**
         * @param uint unsigned int to unpack
         * @param endianness which endian to unpack bytes by
         * @param an array containing the bytes ordered by endianness
         */
        public fun fromUInt(uint: UInt, endianness: Endian): IntArray{
            val int = uint.toInt()
            return toIntArray(int, 4, endianness)
        }
        
        /**
         * @param ulong unsigned long to unpack
         * @param endianness which endian to unpack bytes by
         * @return an array containing the bytes ordered by endianness
         */
        public fun fromULong(ulong: ULong, endianness: Endian): IntArray {
            val long = ulong.toLong()
            return longToIntArray(long, endianness)
        }
        
        /**
         * Unpack any value of size int or smaller into an array
         * @param int the value to unpack into an array
         * @param endianness the endianness by which to unpack
         * @param bytes the number of bytes to unpack
         * @return the array of the bytes ordered by endianness
         */
        private fun toIntArray(int: Int, bytes: Int, endianness: Endian): IntArray {
            if(endianness == Endian.LITTLE) {
                return IntArray(bytes){ i -> ((int shr (i*8)) and 0xFF).toInt()}
            } else {
                return IntArray(bytes){ i -> ((int shr ((bytes-1-i)*8)) and 0xFF).toInt()}
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
                return IntArray(8){ i -> ((long shr (    i*8)) and 0xFF).toInt()}
            } else {
                return IntArray(8){ i -> ((long shr ((7-i)*8)) and 0xFF).toInt()}
            }
            
        }
        
    }
}