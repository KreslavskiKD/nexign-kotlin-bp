package com.nexign.dsl.scenarios.examples.arithmeticscenario

import com.nexign.dsl.base.*
import com.nexign.dsl.base.exceptions.IllegalScenarioArgumentException
import com.nexign.dsl.base.transitions.*

class ComputePerimeter: Operation() {
    override val func: Scenario.() -> TransitionCondition = {
        val a: Double = this.getFromStorage("a")
        val b: Double = this.getFromStorage("b")
        val result: Double = (a + b) * 2
        this.putInStorage("perimeter", result)
        SINGLE_ROUTE
    }
}

class ComputeSquare: Operation() {
    override val func: Scenario.() -> TransitionCondition = {
        val a: Double = this.getFromStorage("a")
        val b: Double = this.getFromStorage("b")
        val result: Double = (a * b)
        this.putInStorage("square", result)
        SINGLE_ROUTE
    }
}

open class ValidateOr: Operation() {
    override val func : Scenario.() -> TransitionCondition = {
        var continueExecution: TransitionCondition = YES
        try {
            val a: Double = this.getFromStorage("a")
            val b: Double = this.getFromStorage("b")
            if (a < 3.0) {
                throw IllegalScenarioArgumentException(Errors.BOUNDS_LESS_ERROR_A)
            }
            if (a > 42.0) {
                throw IllegalScenarioArgumentException(Errors.BOUNDS_MORE_ERROR_A)
            }
            if (b < 3.0) {
                throw IllegalScenarioArgumentException(Errors.BOUNDS_LESS_ERROR_B)
            }
            if (b > 42.0) {
                throw IllegalScenarioArgumentException(Errors.BOUNDS_MORE_ERROR_B)
            }
        } catch (e: IllegalScenarioArgumentException) {
            this.putInStorage("error", e.message!!)
            continueExecution = NO
        }
        continueExecution
    }
}

class PrintResults: Operation() {
    override val func: Scenario.() -> TransitionCondition = {
        val square: Double = this.getFromStorage("square")
        val perimeter: Double = this.getFromStorage("perimeter")
        println("square = $square")
        println("perimeter = $perimeter")
        STOP_EXECUTION
    }
}

class PrintError: Operation() {
    override val func: Scenario.() -> TransitionCondition = {
        val error: String = this.getFromStorage("error")
        println(error)
        STOP_EXECUTION
    }
}
