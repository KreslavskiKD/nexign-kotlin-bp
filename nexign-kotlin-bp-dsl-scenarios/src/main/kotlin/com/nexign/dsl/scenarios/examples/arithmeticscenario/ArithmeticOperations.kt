package com.nexign.dsl.scenarios.examples.arithmeticscenario

import com.nexign.dsl.base.*
import com.nexign.dsl.base.exceptions.IllegalScenarioArgumentException
import com.nexign.dsl.base.transitions.*

val computePerimeter = Operation {
    val a: Double = it.getFromStorage("a")
    val b: Double = it.getFromStorage("b")
    val result: Double = (a + b) * 2
    it.putInStorage("perimeter", result)
    SINGLE_ROUTE
}

val computeSquare = Operation {
    val a: Double = it.getFromStorage("a")
    val b: Double = it.getFromStorage("b")
    val result: Double = (a * b)
    it.putInStorage("square", result)
    SINGLE_ROUTE
}

val validateOr = Operation {
    var continueExecution: TransitionCondition = YES
    try {
        val a: Double = it.getFromStorage("a")
        val b: Double = it.getFromStorage("b")
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
        it.putInStorage("error", e.message!!)
        continueExecution = NO
    }
    continueExecution
}

val printResults = Operation {
    val square: Double = it.getFromStorage("square")
    val perimeter: Double = it.getFromStorage("perimeter")
    println("square = $square")
    println("perimeter = $perimeter")
    STOP_EXECUTION
}

val printError = Operation {
    val error: String = it.getFromStorage("error")
    println(error)
    STOP_EXECUTION
}
