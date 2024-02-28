package com.nexign.dsl.engine

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.nexign.dsl.engine.transport.ScenarioRequest
import com.squareup.moshi.adapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

class Engine : CliktCommand(
    help = """
        Starts the dsl scenarios engine.
        """.trimIndent()
) {

    private val scenariosDir by option(
        "--scenarios-dir", "-s",
        help = """
        Directory where scenarios JARs are stored
        """.trimIndent()
    ).file().default(defaultScenariosDir)

    // TODO
    private val restService: Int by option("-r", "--rest-port",
        help = """
        Start engine as a REST service at specified port
        
        TODO: not yet supported
        """.trimIndent()
    ).int().default(-1)

    // TODO maybe add some more types of interaction later

    private lateinit var application: Application

    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @OptIn(ExperimentalStdlibApi::class)
    override fun run() {

        val jsonAdapter: JsonAdapter<ScenarioRequest> = moshi.adapter<ScenarioRequest>()

        println("Scenarios directory: $scenariosDir")

        if (!scenariosDir.isDirectory) {
            throw IllegalArgumentException("scenarios-dir should be a directory")
        }

        application = Application(scenariosDir)

        while (true) {
            val input = readln()

            if (input == "force stop") {
                application.forceStop()
                break
            }

            if (input == "graceful stop" || input == "stop") {
                application.gracefulStop()
                break
            }

            if (input == "start scenario") {
                val jsonLine = readln()

                val scenarioRequest = jsonAdapter.nullSafe().serializeNulls().fromJson(jsonLine)

                if (scenarioRequest != null) {
                    application.startScenario(scenarioRequest)
                } else {
                    throw JsonDataException("scenario request failed to parse or is null")
                }


            }
        }

    }

    companion object {
        val defaultScenariosDir: File = File("./scenarios") // TODO check if that's OK
    }

}