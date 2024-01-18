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

    inline fun <reified T : Any> Scenario.getFromStorage(name: String) : T {
        return this@getFromStorage.getFromStorage(name, classOpFromStackTraces())
    }

    inline fun <reified T : Any> Scenario.putInStorage(name: String, value: T) {
        this@putInStorage.putInStorage(name, value, classOpFromStackTraces())
    }

}

inline fun Scenario.checkIn() {
    this@checkIn.operationCheckIn("${classOpFromStackTraces()} started")
}

inline fun Scenario.checkOut(transitionCondition: TransitionCondition) {
    this@checkOut.operationCheckIn("${classOpFromStackTraces()} ended with transition condition ${transitionCondition.javaClass.simpleName}")
}

inline infix fun Scenario.functionBuilder(innerFunc: Scenario.() -> TransitionCondition) : TransitionCondition {
    this.checkIn()
    val tc = this.innerFunc()
    this.checkOut(tc)
    return tc
}

object OperationDefault: Operation()
