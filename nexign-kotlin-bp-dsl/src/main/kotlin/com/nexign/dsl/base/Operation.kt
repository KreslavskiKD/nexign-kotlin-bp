package com.nexign.dsl.base

import com.nexign.dsl.base.description.OperationDescription
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.transitions.*

fun interface Operation {
    fun run(scenario: Scenario): OperationResult

    fun getOperationDescription() : OperationDescription =
        OperationDescription(
            operationName = getOperationName(),
            transitions = linkedMapOf(),
            detailedDescription = "",   // TODO: crate a way to get detailed description
        )

    fun getOperationName() : String {
        return if (this.javaClass.simpleName != "") {
            this.javaClass.simpleName
        } else {
            val longName = this.javaClass.name
            val list = longName.split("$")
            list[list.size - 2]
        }
    }

}

object OperationDefault : Operation {
    override fun run(scenario: Scenario): OperationResult {
        return STOP_EXECUTION result None
    }
}

object None : Any()

data class OperationResult (
    val transitionCondition: TransitionCondition,
    val result: Any,
)

infix fun <T: Any> TransitionCondition.result(result: T): OperationResult {
    return OperationResult(this, result)
}
