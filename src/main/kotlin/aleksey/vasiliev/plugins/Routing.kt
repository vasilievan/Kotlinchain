package aleksey.vasiliev.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import aleksey.vasiliev.Block
import aleksey.vasiliev.Block.Companion.getBlockHash
import aleksey.vasiliev.Block.Companion.isBlockValid
import aleksey.vasiliev.BlockProducing
import aleksey.vasiliev.ServerParameters.isFirst
import aleksey.vasiliev.ServerParameters.port
import java.math.BigInteger
import java.util.logging.Logger

suspend fun Application.configureRouting(blockProducing: BlockProducing) {
    Logger.getGlobal().info("Server started at $port port")
    if (isFirst.get()) {
        val producedBlock = blockProducing.produceFirstBlock()
        Logger.getGlobal().info("Produced block: $producedBlock")
        blockProducing.currentBlock.set(producedBlock)
        blockProducing.sendBlockToNodes(producedBlock)
        blockProducing.dbHelper.writeToDB(producedBlock)
    }
    install(ContentNegotiation) {
        json()
    }
    routing {
        post("/update") {
            val frozenBlock = blockProducing.currentBlock.get()
            val possibleBlock = call.receive<Block>()
            val hash = possibleBlock.getBlockHash()
            if (frozenBlock != null) {
                if (hash.isBlockValid() &&
                    frozenBlock.hash == possibleBlock.prev_hash &&
                    BigInteger(possibleBlock.index).subtract(BigInteger(frozenBlock.index)) == BigInteger.ONE
                ) {
                    val isSet = blockProducing.currentBlock.compareAndSet(frozenBlock, possibleBlock)
                    if (isSet) {
                        Logger.getGlobal().info("Updated block: $possibleBlock")
                        blockProducing.sendBlockToNodes(possibleBlock)
                        blockProducing.dbHelper.writeToDB(possibleBlock)
                    } else {
                        val block = blockProducing.findTheFreshestBlock()
                        Logger.getGlobal().info("Updated block: $block")
                        blockProducing.currentBlock.set(block)
                        blockProducing.sendBlockToNodes(possibleBlock)
                        blockProducing.dbHelper.writeToDB(block)
                    }
                }
            } else {
                Logger.getGlobal().info("Updated block: $possibleBlock")
                blockProducing.currentBlock.set(possibleBlock)
                blockProducing.sendBlockToNodes(possibleBlock)
                blockProducing.dbHelper.writeToDB(possibleBlock)
            }
        }
        get("/") {
            if (blockProducing.currentBlock.get() != null) {
                Logger.getGlobal().info("Send block: ${blockProducing.currentBlock.get()}")
                call.respond(blockProducing.currentBlock.get())
            }
        }
    }
}