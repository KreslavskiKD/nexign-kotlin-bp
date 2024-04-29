package com.nexign.dsl.engine

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.nexign.dsl.engine.models.response.ScenarioDescriptionRm
import com.nexign.dsl.engine.models.response.ScenarioStartRm
import com.squareup.moshi.adapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
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

    private lateinit var application: Application

    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @OptIn(ExperimentalStdlibApi::class)
    override fun run() {

        val scenarioRequestJsonAdapter: JsonAdapter<ScenarioStartRm> = moshi.adapter<ScenarioStartRm>()
        val descriptionRequestJsonAdapter: JsonAdapter<ScenarioDescriptionRm> = moshi.adapter<ScenarioDescriptionRm>()

        println("Scenarios directory: $scenariosDir")

        if (!scenariosDir.isDirectory) {
            throw IllegalArgumentException("scenarios-dir should be a directory")
        }

        application = Application(scenariosDir, moshi)

        while (true) {
            when (val input = readln()) {
                "force stop" -> {
                    application.forceStop()
                    break
                }
                "stop" -> {
                    application.gracefulStop()
                    break
                }
                "start scenario" -> {
                    val jsonLine = readln()

                    try {
                        val scenarioRequest = scenarioRequestJsonAdapter.nullSafe().serializeNulls().fromJson(jsonLine)

                        if (scenarioRequest != null) {
                            application.startScenario(scenarioRequest)
                        } else {
                            throw JsonDataException("scenario request failed to parse or is null")
                        }
                    } catch (e: Exception) {
                        println(e.message) // TODO: Make some better handling
                    }
                }
                "get description" -> {
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
                else -> {
                    println("Command \"$input\" unrecognized")
                }
            }
        }
    }

    companion object {
        val defaultScenariosDir: File = File("./scenarios") // TODO check if that's OK
    }

}