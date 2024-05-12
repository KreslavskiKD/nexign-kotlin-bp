package com.nexign.dsl.engine

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.nexign.dsl.engine.models.response.ScenarioDescriptionRm
import com.nexign.dsl.engine.models.response.ScenarioStartRm
import com.nexign.dsl.engine.rest.routers.ScenariosRouter
import com.squareup.moshi.adapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Paths

class Engine : CliktCommand(
    help = """
        Starts the scenarios DSL engine.
        """.trimIndent()
) {

    private val scenariosDir by option(
        "--scenarios-dir", "-s",
        help = """
        Directory where scenarios JARs are stored. Default is $DEFAULT_SCENARIOS_DIR
        """.trimIndent()
    ).file().default(
        File(Paths.get("").toAbsolutePath().toString() + DEFAULT_SCENARIOS_DIR)
    )

    private val testEngine by option(
        "--test-engine", "-t",
        help = """
        Starts engine in Testing mode with interactive console instead of REST.
        """.trimIndent()
    ).flag(default = false)

    private val restPort: Int by option("-r", "--rest-port",
        help = """
        Specifies port to run REST interface at. Default is $DEFAULT_PORT
        """.trimIndent()
    ).int().default(DEFAULT_PORT)

    private lateinit var application: Application

    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @OptIn(ExperimentalStdlibApi::class)
    private val scenarioRequestJsonAdapter: JsonAdapter<ScenarioStartRm> = moshi.adapter<ScenarioStartRm>()
    @OptIn(ExperimentalStdlibApi::class)
    private val descriptionRequestJsonAdapter: JsonAdapter<ScenarioDescriptionRm> = moshi.adapter<ScenarioDescriptionRm>()

    override fun run() {
        println("Scenarios directory: $scenariosDir")

        if (!scenariosDir.isDirectory) {
            throw IllegalArgumentException("scenarios-dir should be a directory")
        }

        application = Application(scenariosDir, moshi)

        if (testEngine) {
            runTestEngine()
        } else {
            runRestService(scenarioRequestJsonAdapter, descriptionRequestJsonAdapter)
        }
    }

    private fun runTestEngine() {
        while (true) {
            when (val input = readln()) {
                Command.FORCE_STOP -> {
                    application.forceStop()
                    break
                }
                Command.STOP -> {
                    application.gracefulStop()
                    break
                }
                Command.START_SCENARIO -> {
                    val jsonLine = readln()

                    try {
                        val scenarioRequest = scenarioRequestJsonAdapter.nullSafe().serializeNulls().fromJson(jsonLine)

                        if (scenarioRequest != null) {
                            runBlocking {
                                application.startScenario(scenarioRequest)
                            }
                        } else {
                            throw JsonDataException("scenario request failed to parse or is null")
                        }
                    } catch (e: Exception) {
                        println(e.message) // TODO: Make some better handling
                    }
                }
                Command.GET_DESCRIPTION -> {
                    val jsonLine = readln()

                    try {
                        val descriptionRequest = descriptionRequestJsonAdapter.nullSafe().serializeNulls().fromJson(jsonLine)

                        if (descriptionRequest != null) {
                            var result = ""
                            runBlocking {
                                result = application.getScenarioDescription(descriptionRequest)
                            }
                            println(result)
                        } else {
                            throw JsonDataException("scenario request failed to parse or is null")
                        }
                    } catch (e: Exception) {
                        println(e.message) // TODO: Make some better handling
                    }
                }
                Command.HELP -> {
                    showHelpForTestEngine()
                }
                else -> {
                    println("Command \"$input\" unrecognized.")
                    showHelpForTestEngine()
                }
            }
        }
    }

    private fun showHelpForTestEngine() {
        println(
            """
                You can use the following commands:
                - `start scenario` - after that you have to provide a json on a separate line. An example of such json is shown below. This command starts a specified scenario with provided input.
                - `stop` - stops the Engine and all scenarios gracefully (when such mechanism will be implemented)
                - `force stop` - stops the Engine and all scenarios ungracefully
                - `get description` - after that you have to provide a json on a separate line. An example of such json is shown below. This command generates description of provided scenario in one of three types: text in english, dot notation or png picture.
                
                Example JSONs:
                - for `start scenario`: 
                ```json
                {"scenarioClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario","inputClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput","input":"{\"a\":12.0,\"b\":5.5}"}
                ```
                - for `get description`:
                ```json
                {"scenarioClassName":"com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario","descriptionType":"PICTURE","addErrorRouting":"NO"}
                ```
            """.trimIndent()
        )
    }

    private fun runRestService(
        startScenarioAdapter: JsonAdapter<ScenarioStartRm>,
        descriptionAdapter: JsonAdapter<ScenarioDescriptionRm>
    ) {
        embeddedServer(Netty, port = restPort) {
            this.apply(
                ScenariosRouter(
                    startScenarioAdapter,
                    descriptionAdapter,
                    application,
                )::routeScenarios
            )
        }.start(wait = true)
    }

    companion object {
        const val DEFAULT_SCENARIOS_DIR = "./scenarios"
        const val DEFAULT_PORT = 8080

        object Command {
            const val STOP = "stop"
            const val FORCE_STOP = "force stop"
            const val START_SCENARIO = "start scenario"
            const val GET_DESCRIPTION = "get description"
            const val HELP = "help"
        }
    }
}
