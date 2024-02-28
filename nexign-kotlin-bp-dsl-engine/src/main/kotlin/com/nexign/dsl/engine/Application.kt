package com.nexign.dsl.engine

import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.engine.transport.ScenarioRequest
import com.nexign.dsl.engine.worker.Worker
import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class Application(
    private val scenariosDirectory: File,
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

                if (!scenarioClazz.isAssignableFrom(Scenario::class.java)) {
                    throw IllegalArgumentException("it is not a Scenario class") // TODO change to custom
                }

                val inputClazz = classLoader.loadClass(scenarioRequest.inputClassName)

                if (!inputClazz.isAssignableFrom(Input::class.java)) {
                    throw IllegalArgumentException("it is not an Input class") // TODO change to custom
                }

                val constructor = inputClazz.kotlin.primaryConstructor
                    ?: throw Exception("constructor not found") // TODO change to custom

                worker.consume(
                    input = constructor.call(*scenarioRequest.inputConstructorParameters.toTypedArray()) as Input,
                    clazz = scenarioClazz.kotlin as KClass<out Scenario>,   // Unchecked cast is verified before
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
        // TODO here probably should be saving the scenarios states, when this feature will be done
    }
}