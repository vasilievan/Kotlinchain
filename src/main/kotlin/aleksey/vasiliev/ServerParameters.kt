package aleksey.vasiliev

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import java.util.concurrent.atomic.AtomicBoolean

object ServerParameters {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    var port = 0
    var isFirst = AtomicBoolean(false)
    var dbName: String = ""
    lateinit var nodesToSend: Set<String>
}