package aleksey.vasiliev

import kotlinx.serialization.Serializable
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Serializable
data class Block(
    val index: String,
    val prev_hash: String,
    val hash: String,
    val data: String,
    val nonce: String
) {
    companion object {
        private val ONE_MILLION = BigInteger.valueOf(1000000)

        private fun concatenateBlock(
            index: BigInteger,
            prev_hash: BigInteger,
            data: String,
            nonce: BigInteger
        ): BigInteger = index.add(prev_hash).add(BigInteger(data.toByteArray(StandardCharsets.UTF_8))).add(nonce)

        fun getBlockHash(
            index: BigInteger,
            prev_hash: BigInteger,
            data: String,
            nonce: BigInteger
        ): BigInteger {
            val concatenated = concatenateBlock(index, prev_hash, data, nonce)
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(concatenated.toByteArray())
            return BigInteger(hash)
        }

        fun Block.getBlockHash(): BigInteger {
            val concatenated =
                concatenateBlock(BigInteger(this.index), BigInteger(this.prev_hash), this.data, BigInteger(this.nonce))
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(concatenated.toByteArray())
            return BigInteger(hash)
        }

        fun BigInteger.isBlockValid(): Boolean = this.mod(ONE_MILLION) == BigInteger.ZERO
    }
}