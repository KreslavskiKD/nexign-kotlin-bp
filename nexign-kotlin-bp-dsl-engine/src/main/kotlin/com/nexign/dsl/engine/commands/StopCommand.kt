package com.nexign.dsl.engine.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.nexign.dsl.engine.Application

class StopCommand(
    private val application: Application,
) : CliktCommand(
    name = "stop",
    help = """
        Stops the Engine daemon.
        """.trimIndent()
) {

    private val force: Boolean by option("-f", "--force",
        help =
        """
        Stops engine immediately, ungracefully towards the scenarios currently executing
        """.trimIndent())
        .flag()


    override fun run() {
        if (force) {
            application.forceStop()
        } else {
            application.gracefulStop()
        }
    }
}