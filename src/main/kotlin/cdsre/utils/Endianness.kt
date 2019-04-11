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
            if(endianness == Endian.LITTLE)
                return ((b2 shl 8) + b1).toUShort()
            else
                return ((b1 shl 8) + b2).toUShort()
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
            if(endianness == Endian.LITTLE)
                return ((b4 shl 24) + (b3 shl 16) + (b2 shl 8) + b1).toUInt()
            else
                return ((b1 shl 24) + (b2 shl 16) + (b3 shl 8) + b4).toUInt()
        }
        
        /**
         * @param ushort unsigned short to unpack
         * @param endianness which endian to unpack bytes by
         * @param an array containing the bytes ordered by endianness
         */
        public fun fromUShort(ushort: UShort, endianness: Endian): IntArray{
            val int = ushort.toInt()
            if(endianness == Endian.LITTLE) {
                return intArrayOf(
                        ((int shr 0) and 0xFF),
                        ((int shr 8) and 0xFF))
            } else {
                return intArrayOf(
                        ((int shr 8) and 0xFF),
                        ((int shr 0) and 0xFF))
            }
        }
        
        /**
         * @param uint unsigned int to unpack
         * @param endianness which endian to unpack bytes by
         * @param an array containing the bytes ordered by endianness
         */
        public fun fromUInt(uint: UInt, endianness: Endian): IntArray{
            val int = uint.toInt()
            if(endianness == Endian.LITTLE) {
                return intArrayOf(
                        ((int shr 0) and 0xFF),
                        ((int shr 8) and 0xFF),
                        ((int shr 16) and 0xFF),
                        ((int shr 24) and 0xFF))
            } else {
                return intArrayOf(
                        ((int shr 24) and 0xFF),
                        ((int shr 16) and 0xFF),
                        ((int shr 8) and 0xFF),
                        ((int shr 0) and 0xFF))
            }
        }
        
    }
}