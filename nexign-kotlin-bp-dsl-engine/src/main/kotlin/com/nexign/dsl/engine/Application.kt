package com.nexign.dsl.engine

import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.engine.transport.ScenarioRequest
import com.nexign.dsl.engine.worker.Worker
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.reflect.KClass

class Application(
    private val scenariosDirectory: File,
    private val moshi: Moshi,
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private var stopping = false

    private val classLoader : ClassLoader

    init {
        val urls = mutableListOf<URL>()
        scenariosDirectory.walkTopDown().filter { it.isFile }.forEach {
            urls.add(it.toURI().toURL())
        }

        classLoader = URLClassLoader.newInstance(urls.toTypedArray())
    }

    fun startScenario(scenarioRequest: ScenarioRequest) {
        if (!stopping) {
            println("Starting scenario...")

            scope.launch {
                val worker = Worker()

                val scenarioClazz = classLoader.loadClass(scenarioRequest.scenarioClassName)

                // TODO FIX
//                if (!scenarioClazz.isAssignableFrom(Scenario::class.java)) {
//                    throw IllegalArgumentException("it is not a Scenario class") // TODO change to custom
//                }

                val inputClazz = classLoader.loadClass(scenarioRequest.inputClassName)

                // TODO FIX
//                if (!inputClazz.isAssignableFrom(Input::class.java)) {
//                    throw IllegalArgumentException("${inputClazz.name} is not an Input class") // TODO change to custom
//                }

                val jsonAdapter: JsonAdapter<out Input> = moshi.adapter(inputClazz) as JsonAdapter<out Input>

                worker.consume(
                    input = jsonAdapter.nullSafe().serializeNulls().fromJson(scenarioRequest.input)
                        ?: throw IllegalArgumentException("it is not an instance of provided ${inputClazz.name}"),  // TODO change to custom
                    clazz = scenarioClazz.kotlin as KClass<out Scenario>,   // Unchecked cast is verified before TODO
                )

                worker.startScenario()
            }
        } else {
            println("Can't start scenario, the engine is stopping.")
        }
    }

    fun forceStop() {
        stopping = true
        scope.cancel() // Cancels all running coroutines
        println("All scenarios stopped ungracefully.")
    }

    fun gracefulStop() {
        stopping = true
        println("Engine is stopping")
        // TODO here probably should be saving the scenarios states, when this feature will be done ENGINE-8
    }
}