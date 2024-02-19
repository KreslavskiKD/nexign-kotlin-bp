package com.nexign.dsl.engine.commands

import com.github.ajalt.clikt.core.CliktCommand

class StopCommand : CliktCommand(
    name = "stop",
    help = """
        Stops the Engine daemon.
        """.trimIndent()
) {


    override fun run() {
        TODO("Not yet implemented")
    }
}