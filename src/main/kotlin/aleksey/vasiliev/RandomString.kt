package aleksey.vasiliev

import java.util.Random

object RandomString {
    var seed = 3141592653L
        set(value) {
            random = Random(value)
        }

    private var random = Random(seed)

    private val symbolsAmount = 256
    private val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun getRandomString() = (0 until symbolsAmount).map { alphabet[(alphabet.size * random.nextFloat()).toInt()] }
        .joinToString(separator = "")
}