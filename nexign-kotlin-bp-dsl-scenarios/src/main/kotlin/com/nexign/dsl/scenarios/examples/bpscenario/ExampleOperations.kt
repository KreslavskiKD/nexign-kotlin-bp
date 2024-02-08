package com.nexign.dsl.scenarios.examples.bpscenario

import com.nexign.dsl.base.*
import com.nexign.dsl.base.transitions.*

import kotlin.random.Random

val getAbonentInfo = Operation {
    // Do something
    SINGLE_ROUTE
}

val prolongAction = Operation {
    // Do something
    SINGLE_ROUTE
}

val activateAction = Operation {
    // Do something
    SINGLE_ROUTE
}

val cancelActionActivation = Operation {
    // Do something
    SINGLE_ROUTE
}


open class NotifyAction(
    private val message: String,
) : Operation {

    override fun run(scenario: Scenario): TransitionCondition {
        // Do something
        println("I am notified about $message")

        return SINGLE_ROUTE
    }
}

val checkAbonentActions = Operation {
    var transitionCondition: TransitionCondition = YES

    // decision imitation
    val isActionAlreadyActive: Boolean = Random.nextBoolean()
    // Do something
    if (isActionAlreadyActive) {
        transitionCondition = NO
    }
    transitionCondition
}

val writeOffMoney = Operation {
    var transitionCondition: TransitionCondition = YES

    // decision imitation
    val isThereEnoughMoney: Boolean = Random.nextBoolean()
    // Do something
    if (isThereEnoughMoney) {
        transitionCondition = NO
    }
    transitionCondition
}

val defaultErrorHandling = Operation {
    val error: String = it.getFromStorage("error")
    println(error)
    STOP_EXECUTION
}

val specialErrorHandling = Operation {
    val error: String = it.getFromStorage("error")
    println("Some super error handling: $error")
    STOP_EXECUTION
}
