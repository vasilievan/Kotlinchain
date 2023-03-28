package aleksey.vasiliev

import aleksey.vasiliev.ServerParameters.dbName
import aleksey.vasiliev.ServerParameters.isFirst
import aleksey.vasiliev.ServerParameters.nodesToSend
import aleksey.vasiliev.ServerParameters.port
import aleksey.vasiliev.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

fun main(args: Array<String>) {
    cmdArgsParser(args)
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun cmdArgsParser(args: Array<String>) {
    val summaryOptions = Options()
    val nodeOption = Option(
        "n",
        "nodes",
        true,
        "Specify the blockchain nodes in format: http://127.0.0.1:8080,http://127.0.0.1:8081"
    )
    nodeOption.args = 1
    nodeOption.setOptionalArg(false)
    val dbOption = Option(
        "d",
        "database",
        true,
        "Specify where to write nodes to check them if you want"
    )
    dbOption.args = 1
    dbOption.setOptionalArg(true)
    val portOption = Option("p", "port", true, "Specify the port to open the server")
    portOption.args = 1
    portOption.setOptionalArg(false)
    portOption.argName = "port "
    val firstOption = Option("f", "first", false, "If set, the server will generate the first block")
    firstOption.setOptionalArg(true)
    firstOption.argName = "first block "
    summaryOptions.addOption(portOption)
    summaryOptions.addOption(firstOption)
    summaryOptions.addOption(nodeOption)
    summaryOptions.addOption(dbOption)
    val defaultParser = DefaultParser()
    val parsedCmdLine = defaultParser.parse(summaryOptions, args)
    if (parsedCmdLine.hasOption("p")) {
        val parsedPort = parsedCmdLine.getOptionValues("p")[0].toIntOrNull()
            ?: throw IllegalArgumentException("Port should be an Int number!")
        port = parsedPort
    } else {
        throw IllegalArgumentException("Please, specify the server port!")
    }
    if (parsedCmdLine.hasOption("n")) {
        val nodes = parsedCmdLine.getOptionValues("n")[0]
        val splitNodes = nodes.split(",")
        if (splitNodes.any { !it.matches(Regex("https?://((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}:\\d{1,5}")) }) {
            throw IllegalArgumentException("Incorrect node address!")
        }
        nodesToSend = splitNodes.toSet()
    } else {
        throw IllegalArgumentException("Please, specify the nodes!")
    }
    if (parsedCmdLine.hasOption("f")) isFirst.set(true) else isFirst.set(false)
    if (parsedCmdLine.hasOption("d")) dbName = parsedCmdLine.getOptionValues("d")[0]
}

fun Application.module() {
    val blockProducing = BlockProducing()
    launch(Dispatchers.IO) {
        configureRouting(blockProducing)
    }
    launch(Dispatchers.IO) {
        blockProducing.produceBlocks()
    }
}