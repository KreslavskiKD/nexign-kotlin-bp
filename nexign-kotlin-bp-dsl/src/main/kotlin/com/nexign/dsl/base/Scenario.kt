package com.nexign.dsl.base

import com.nexign.dsl.base.description.ScenarioDescription
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.transitions.START_EXECUTION
import com.nexign.dsl.base.transitions.STOP_EXECUTION
import com.nexign.dsl.base.transitions.TransitionCondition

abstract class Scenario(store: MutableMap<String, Any>) : Operation() {
    open val specification : Specification = Specification()

    val storage: MutableMap<String, Any> = store

    public override val func: Scenario.() -> TransitionCondition = { START_EXECUTION }

    inline fun <reified T : Any> getFromStorage(name: String) : T {
        return storage[name] as T
    }

    inline fun <reified T : Any> putInStorage(name: String, value: T) {
        storage[name] = value
    }

    fun getDescription() : ScenarioDescription {
        return specification.routing.getScenarioDescription(

            scenarioName = this::class.java.simpleName,
            scenarioDetailedDescription = "" // TODO: here should be some logic to get details from e.g. KDoc
        )
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


