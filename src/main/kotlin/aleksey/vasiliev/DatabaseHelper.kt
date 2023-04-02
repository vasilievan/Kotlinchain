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
}