package com.nexign.dsl.base

import com.nexign.dsl.base.description.OperationDescription
import com.nexign.dsl.base.transitions.*

fun interface Operation {
    fun run(scenario: Scenario): TransitionCondition

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
    override fun run(scenario: Scenario): TransitionCondition {
        return STOP_EXECUTION
    }
}
