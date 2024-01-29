package com.nexign.dsl.base

import com.nexign.dsl.base.description.OperationDescription
import com.nexign.dsl.base.transitions.*

fun interface Operation {
    fun run(scenario: Scenario): TransitionCondition

    fun getOperationDescription() : OperationDescription =
        OperationDescription(
            operationName = this.javaClass.simpleName,
            transitions = linkedMapOf(),
            detailedDescription = "",   // TODO: crate a way to get detailed description
        )

}

object OperationDefault : Operation {
    override fun run(scenario: Scenario): TransitionCondition {
        return STOP_EXECUTION
    }
}
