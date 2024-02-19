package com.nexign.dsl.engine

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.nexign.dsl.engine.commands.StartCommand
import com.nexign.dsl.engine.commands.StartScenarioCommand
import com.nexign.dsl.engine.commands.StopCommand

class Engine : CliktCommand() {

    override fun run() {

    }

    init {
        subcommands(
            StartCommand(),
            StartScenarioCommand(),
            StopCommand(),
        )
    }
}