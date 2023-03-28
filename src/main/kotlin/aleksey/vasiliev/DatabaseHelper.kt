package aleksey.vasiliev

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import aleksey.vasiliev.ServerParameters.dbName
import java.io.File

class DatabaseHelper {
    private val file = if (dbName.isEmpty()) File("${System.currentTimeMillis()}.db") else File(dbName)

    init {
        if (!file.exists()) file.createNewFile()
    }

    @Synchronized
    fun writeToDB(block: Block) {
        with(file) {
            appendText(Json.encodeToString(block))
            appendText("\n")
        }
    }

    companion object {
        fun isBlockchainCorrect(firstFile: File, secondFile: File): Boolean {
            val db1: Set<Block>
            val db2: Set<Block>
            with(firstFile) {
                db1 = this.readLines().map { Json.decodeFromString<Block>(it) }.toSet()
            }
            with(secondFile) {
                db2 = this.readLines().map { Json.decodeFromString<Block>(it) }.toSet()
            }
            val difference = db1.subtract(db2)
            val indices = difference.map { it.index }.toSet()
            return difference.size == indices.size
        }
    }
}