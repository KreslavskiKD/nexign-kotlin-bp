package com.nexign.dsl.engine.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import kotlin.concurrent.thread

class StartCommand : CliktCommand(
    name = "start",
    help = """
        Starts the Engine daemon.
        """.trimIndent()
) {
    private val scenariosDir by option(
        "--scenarios-dir", "-s",
        help = "Directory where scenarios JARs are stored"
    ).file()
    private val runConfigsDir by option(
        "--run-configs-dir", "-r",
        help = "Directory with run configuration files for scenarios"
    ).file()

    override fun run() {
        // TODO: Implement logic here
        println("Start command executed")
        println("Scenarios directory: $scenariosDir")
        println("Run configs directory: $runConfigsDir")

        var allow = false

        synchronized(MainPoolDaemon) {
            if (!MainPoolDaemon.isStarted) {
                MainPoolDaemon.isStarted = true
                allow = true
            }
        }

        if (allow) {
            MainPoolDaemon.thread.start()
        }
    }
}

object MainPoolDaemon {
    var isStarted = false

    var thread: Thread = thread(
        start = false,
        isDaemon = true,
        name = "MainPoolDaemon"
    ) {

    }
}
