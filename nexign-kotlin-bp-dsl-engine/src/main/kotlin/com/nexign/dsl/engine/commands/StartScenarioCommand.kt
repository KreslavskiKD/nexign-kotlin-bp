package com.nexign.dsl.engine.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file

class StartScenarioCommand : CliktCommand(
    name = "start-scenario",
    help = """
        Starts a specified scenario.
        """.trimIndent()
) {
    private val scenarioFile by option(
        "--scenario-file-name", "-f",
        help = "Name of the scenario configuration file"
    ).file()

    override fun run() {
        // TODO: Implement logic here
        println("Start command executed")
        println("Scenario file: $scenarioFile")
    }
}