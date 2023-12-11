package com.nexign.dsl.scenarios.examples.bpscenario

import com.nexign.dsl.base.*

import kotlin.random.Random

open class GetAbonentInfo : Operation() {

    override val func: Scenario.() -> TransitionCondition = {
        // Do something
        SINGLE_ROUTE
    }
}

open class ProlongAction : Operation() {

    override val func: Scenario.() -> TransitionCondition = {
        // Do something
        SINGLE_ROUTE
    }
}

open class ActivateAction : Operation() {

    override val func: Scenario.() -> TransitionCondition = {
        // Do something
        SINGLE_ROUTE
    }
}

open class CancelActionActivation : Operation() {

    override val func: Scenario.() -> TransitionCondition = {
        // Do something
        SINGLE_ROUTE
    }
}


open class NotifyAction(
    private val message: String,
) : Operation() {

    override val func: Scenario.() -> TransitionCondition = {
        // Do something
        println("I am notified about $message")

        SINGLE_ROUTE
    }
}

open class CheckAbonentActions : Operation() {

    override val func: Scenario.() -> TransitionCondition = {
        var transitionCondition: TransitionCondition = YES

        // decision imitation
        val isActionAlreadyActive: Boolean = Random.nextBoolean()
        // Do something
        if (isActionAlreadyActive) {
            transitionCondition = NO
        }
        transitionCondition
    }
}

open class WriteOffMoney : Operation() {

    override val func: Scenario.() -> TransitionCondition = {
        var transitionCondition: TransitionCondition = YES

        // decision imitation
        val isThereEnoughMoney: Boolean = Random.nextBoolean()
        // Do something
        if (isThereEnoughMoney) {
            transitionCondition = NO
        }
        transitionCondition
    }
}