package com.nexign.dsl.base

import com.nexign.dsl.base.description.OperationDescription
import com.nexign.dsl.base.transitions.*

open class Operation {
    protected open val func : Scenario.() -> TransitionCondition = { TransitionCondition() }

    fun run(scenario: Scenario): TransitionCondition {
        return this.func.invoke(scenario)
    }

    fun getOperationDescription() : OperationDescription =
        OperationDescription(
            operationName = this.javaClass.simpleName,
            transitions = linkedMapOf(),
            detailedDescription = "",   // TODO: crate a way to get detailed description
        )

}

object OperationDefault: Operation()
