package aleksey.vasiliev

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import aleksey.vasiliev.Block.Companion.isBlockValid
import aleksey.vasiliev.ServerParameters.client
import java.math.BigInteger
import java.net.ConnectException
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Logger

class BlockProducing {
    val dbHelper = DatabaseHelper()
    val currentBlock: AtomicReference<Block> = AtomicReference<Block>()
    val nodesToSend = ServerParameters.nodesToSend

    suspend fun produceBlocks() {
        while (true) {
            val frozenBlock = currentBlock.get()
            if (frozenBlock != null) {
                val producedBlock = produceBlock(frozenBlock)
                val theFreshestBlock = findTheFreshestBlock()
                if (frozenBlock == theFreshestBlock) {
                    val isSet = currentBlock.compareAndSet(frozenBlock, producedBlock)
                    if (isSet) {
                        Logger.getGlobal().info("Produced block: $producedBlock")
                        sendBlockToNodes(producedBlock)
                        dbHelper.writeToDB(producedBlock)
                    } else {
                        Logger.getGlobal().info("Updated block: $theFreshestBlock")
                        currentBlock.set(theFreshestBlock)
                        sendBlockToNodes(theFreshestBlock)
                        dbHelper.writeToDB(theFreshestBlock)
                    }
                } else {
                    Logger.getGlobal().info("Updated block: $theFreshestBlock")
                    currentBlock.set(theFreshestBlock)
                    sendBlockToNodes(theFreshestBlock)
                    dbHelper.writeToDB(theFreshestBlock)
                }
            }
        }
    }

    suspend fun sendBlockToNode(node: String, producedBlock: Block) {
        try {
            client.post("$node/update") {
                contentType(ContentType.Application.Json)
                setBody(producedBlock)
            }
        } catch (_: ConnectException) {
        }
    }

    @Synchronized
    fun sendBlockToNodes(producedBlock: Block) = runBlocking {
        for (node in nodesToSend) {
            sendBlockToNode(node, producedBlock)
        }
    }

    @Synchronized
    fun produceBlock(block: Block): Block {
        val index = BigInteger(block.index).add(BigInteger.ONE)
        val prev_hash = BigInteger(block.hash)
        val data = RandomString.getRandomString()
        var nonce = BigInteger.ZERO
        var hash = Block.getBlockHash(index, prev_hash, data, nonce)
        var isValid = hash.isBlockValid()
        while (!isValid) {
            nonce = nonce.add(BigInteger.ONE)
            hash = Block.getBlockHash(index, prev_hash, data, nonce)
            isValid = hash.isBlockValid()
        }
        return Block(index.toString(), prev_hash.toString(), hash.toString(), data, nonce.toString())
    }

    @Synchronized
    fun produceFirstBlock(): Block {
        val index = BigInteger.ZERO
        val prev_hash = BigInteger.ZERO
        val data = RandomString.getRandomString()
        var nonce = BigInteger.ZERO
        var hash = Block.getBlockHash(index, prev_hash, data, nonce)
        var isValid = hash.isBlockValid()
        while (!isValid) {
            nonce = nonce.add(BigInteger.ONE)
            hash = Block.getBlockHash(index, prev_hash, data, nonce)
            isValid = hash.isBlockValid()
        }
        return Block(index.toString(), prev_hash.toString(), hash.toString(), data, nonce.toString())
    }

    suspend fun findTheFreshestBlock(): Block {
        val frozenBlock = AtomicReference(currentBlock.get())
        for (node in nodesToSend) {
            val block = getBlockFromNode(node)
            if (block != null &&
                block.index > frozenBlock.get().index
            ) {
                frozenBlock.set(block)
            }
        }
        return frozenBlock.get()
    }

    private suspend fun getBlockFromNode(node: String): Block? {
        return try {
            val response = client.get("$node/")
            response.body<Block>()
        } catch (e: Exception) {
            null
        }
    }
}