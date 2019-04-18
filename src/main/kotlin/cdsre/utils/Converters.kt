package cdsre.utils

object Converters {

    fun <T> safeConvert(func: () -> T): T? {
        return try {
            func()
        } catch (e: Exception) {
            null
        }
    }

    fun <T, K> safeConvert(func: (K) -> T, arg: K): T? {
        return try {
            func(arg)
        } catch (e: Exception) {
            null
        }
    }

}
