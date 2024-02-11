package com.nexign.dsl.scenarios.examples.arithmeticscenario

import com.nexign.dsl.base.exceptions.IllegalScenarioArgumentException
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.specification.routing
import com.nexign.dsl.base.specification.specification
import com.nexign.dsl.base.transitions.*
import com.nexign.dsl.base.*
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.scenario.data.Results

data class ArithmeticInput(
    val a: Double,
    val b: Double,
) : Input()

class ArithmeticScenario(private val input: ArithmeticInput) : Scenario(input) {

    override val specification: Specification = specification {
        routing = routing {
            start(validateOr binary {
                yes(computeSquare next computePerimeter next printResults)
                no(printError)
            })
        }
    }

    override val results = object : Results() {
        var perimeter: Double = 0.0
        var square: Double = 0.0
    }

    private val computePerimeter = Operation {
        val perimeter: Double = (input.a + input.b) * 2
        SINGLE_ROUTE result perimeter
    }

    private val computeSquare = Operation {
        val square: Double = (input.a * input.b)
        SINGLE_ROUTE result square
    }

    private val validateOr = Operation {
        var continueExecution: TransitionCondition = YES
        try {
            val a: Double = input.a
            val b: Double = input.b
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
        continueExecution result None
    }

    private val printResults = Operation {
        val square: Double = it.getFromStorage("square")
        val perimeter: Double = it.getFromStorage("perimeter")
        println("square = $square")
        println("perimeter = $perimeter")
        STOP_EXECUTION result None
    }

    private val printError = Operation {
        val error: String = it.getFromStorage("error")
        println(error)
        STOP_EXECUTION result None
    }
}
