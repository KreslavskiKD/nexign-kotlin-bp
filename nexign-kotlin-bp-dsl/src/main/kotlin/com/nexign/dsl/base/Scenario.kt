package com.nexign.dsl.base

import com.nexign.dsl.base.description.OperationDescription
import com.nexign.dsl.base.description.RunStage
import com.nexign.dsl.base.description.ScenarioDescription
import com.nexign.dsl.base.exceptions.IllegalScenarioArgumentException

abstract class Scenario(store: MutableMap<String, Any>) : Operation() {
    private val storage: MutableMap<String, Any> = store

    private val lastRun: MutableList<RunStage> = mutableListOf()

    override val func: Scenario.() -> TransitionCondition
        get() = { START_EXECUTION }

    infix fun run(processFunc: (Map<String, Any>) -> Unit) {
        start(this)
        processFunc(storage)
    }

    inline fun <reified T : Any> getFromStorage(name: String, callerOperationName: String) : T {
        return fromStorage(name, callerOperationName) as T
    }

    inline fun <reified T : Any> putInStorage(name: String, value: T, callerOperationName: String) {
        inStorage(name, value, callerOperationName)
    }

    fun getScenarioDescription(): ScenarioDescription {
        val startingOperation = specification[START_EXECUTION]
            ?: throw IllegalStateException("No starting operation in scenario")     // TODO should be some custom exception

        val visited: MutableSet<Operation> = mutableSetOf()
        val opsDescrs: MutableMap<Operation, OperationDescription> = mutableMapOf()

        var currentOps = listOf(startingOperation)
        var nextOps = mutableListOf<Operation>()

        opsDescrs[startingOperation] = startingOperation.getOperationDescription()

        while (currentOps.isNotEmpty()) {
            for (op in currentOps) {
                for (nop in op.specification) {
                    if (!visited.contains(nop.value)) {
                        opsDescrs[nop.value] = nop.value.getOperationDescription()
                        visited.add(nop.value)
                        nextOps.add(nop.value)
                    }

                    opsDescrs[op]?.transitions?.set(nop.key, opsDescrs[nop.value]!!)
                }

            }
            currentOps = nextOps
            nextOps = mutableListOf()
        }


        return ScenarioDescription(
            scenarioName = this.javaClass.simpleName,
            startingOperation = opsDescrs[startingOperation]!!,
            detailedDescription = ""            // here we will need to get some KDoc probably
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
}

@DslMarker
annotation class ScenarioDSL


