package com.nexign.dsl.base

import com.nexign.dsl.base.description.RunStage
import com.nexign.dsl.base.description.ScenarioDescription
import com.nexign.dsl.base.exceptions.IllegalScenarioArgumentException
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.transitions.START_EXECUTION
import com.nexign.dsl.base.transitions.STOP_EXECUTION
import com.nexign.dsl.base.transitions.TransitionCondition

abstract class Scenario(store: MutableMap<String, Any>) : Operation() {
    open val specification : Specification = Specification()
    private val storage: MutableMap<String, Any> = store

    private val lastRun: MutableList<RunStage> = mutableListOf()

    public override val func: Scenario.() -> TransitionCondition = { START_EXECUTION }

    inline fun <reified T : Any> getFromStorage(name: String, callerOperationName: String) : T {
        return fromStorage(name, callerOperationName) as T
    }

    inline fun <reified T : Any> putInStorage(name: String, value: T, callerOperationName: String) {
        inStorage(name, value, callerOperationName)
    }

    fun getDescription() : ScenarioDescription {
        return specification.routing.getScenarioDescription(
            scenarioName = this.javaClass.simpleName,
            scenarioDetailedDescription = "" // TODO: here should be some logic to get details from e.g. KDoc
        )
    }

    fun inStorage(name: String, value: Any, callerOperationName: String) {
        lastRun.add(RunStage("$callerOperationName put value $value named $name" ))

        storage[name] = value
    }

    fun fromStorage(name: String, callerOperationName: String) : Any {
        lastRun.add(RunStage("$callerOperationName wanted to get value named $name" ))

        return if (storage[name] != null) {
            val value = storage[name]
            lastRun.add(RunStage("$callerOperationName got value $value named $name" ))
            storage[name]!!
        } else {
            lastRun.add(RunStage("$callerOperationName wanted to get value named $name but no such value exists" ))
            throw IllegalScenarioArgumentException("No value with name $name exists")
        }
    }

    fun operationCheckIn(message: String) {
        lastRun.add(RunStage(message))
    }

    fun getLastRun(): List<RunStage> {
        return lastRun.toList()
    }

    companion object {
        val start = object : Operation() {
            override val func: Scenario.() -> TransitionCondition = { START_EXECUTION }
        }

        val end = object : Operation() {
            override val func: Scenario.() -> TransitionCondition = { STOP_EXECUTION }
        }
    }
}


