package com.nexign.dsl.engine

import com.nexign.dsl.base.description.DescriptionType
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.exceptions.NexignBpIllegalClassProvidedException
import com.nexign.dsl.base.specification.Specifiable
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.engine.models.response.ScenarioDescriptionRm
import com.nexign.dsl.engine.models.response.ScenarioStartRm
import com.nexign.dsl.engine.worker.Worker
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible

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

    fun startScenario(scenarioRequest: ScenarioStartRm): String {
        if (stopping) {
            return "Can't start scenario, the engine is stopping."
        }

        scope.launch {
            val worker = Worker()

            val scenarioClazz = classLoader.loadClass(scenarioRequest.scenarioClassName)

            if (!scenarioClazz.kotlin.isSubclassOf(Scenario::class)) {
                throw NexignBpIllegalClassProvidedException("it is not a Scenario class")
            }

            val inputClazz = classLoader.loadClass(scenarioRequest.inputClassName)

            if (!inputClazz.kotlin.isSubclassOf(Input::class)) {
                throw NexignBpIllegalClassProvidedException("${inputClazz.name} is not an Input class")
            }

            val jsonAdapter: JsonAdapter<out Input> = moshi.adapter(inputClazz) as JsonAdapter<out Input>

            worker.consume(
                input = jsonAdapter.nullSafe().serializeNulls().fromJson(scenarioRequest.input)
                    ?: throw NexignBpIllegalClassProvidedException("it is not an instance of provided ${inputClazz.name}"),
                clazz = scenarioClazz.kotlin as KClass<out Scenario>,   // Unchecked cast is verified before
            )

            worker.startScenario()
        }
        return ""
    }

    suspend fun getScenarioDescription(descriptionRequest: ScenarioDescriptionRm): String {
        if (stopping) {
            return "The engine is stopping."
        }

        var result: String = ""

        val job = scope.launch {
            val scenarioClazz = classLoader.loadClass(descriptionRequest.scenarioClassName)

            validateClassIsScenarioClassWithSpecification(scenarioClazz)

            val specificationMethod = scenarioClazz.kotlin.companionObject!!.members.stream()
                .filter{ it.name == "specification" }
                .findFirst()
                .get()

            specificationMethod.isAccessible = true

            val specification = specificationMethod.call(scenarioClazz.kotlin.companionObjectInstance) as Specification
            val description = Scenario.getDescription(scenarioClazz.simpleName, specification)

            result = when (descriptionRequest.descriptionType) {
                DescriptionType.TEXT -> {
                    description.toText(descriptionRequest.addErrorRouting)
                }

                DescriptionType.DOT_FILE -> {
                    description.toDot(descriptionRequest.addErrorRouting)
                }

                DescriptionType.PICTURE -> {
                    description.toPicture(descriptionRequest.addErrorRouting)
                }
            }
        }

        job.join()
        return result
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

    private fun validateClassIsScenarioClassWithSpecification(clazz: Class<*>?) {
        if (clazz == null) {
            throw NexignBpIllegalClassProvidedException("Could not load Scenario class")
        }

        if (!clazz.kotlin.isSubclassOf(Scenario::class)) {
            throw NexignBpIllegalClassProvidedException("${clazz.name} is not a Scenario class")
        }

        if (clazz.kotlin.companionObject == null) {
            throw NexignBpIllegalClassProvidedException("${clazz.name} does not have a companion object")
        }

        if (!clazz.kotlin.companionObject!!.isSubclassOf(Specifiable::class)) {
            throw NexignBpIllegalClassProvidedException("${clazz.name} companion object is not Specifiable")
        }
    }
}