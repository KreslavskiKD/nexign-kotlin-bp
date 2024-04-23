package scenarios.arithmeticscenario

import com.nexign.dsl.base.exceptions.IllegalScenarioArgumentException
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.specification.routing
import com.nexign.dsl.base.specification.specification
import com.nexign.dsl.base.transitions.*
import com.nexign.dsl.base.*
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.scenario.data.Results
import com.nexign.dsl.scenarios.examples.arithmeticscenario.Errors

data class ArithmeticInput(
    val a: Double,
    val b: Double,
) : Input

data class ArithmeticResults(
    var perimeter: Double,
    var square: Double,
    override var error: String,
) : Results

class ArithmeticScenario(override val input: ArithmeticInput) : Scenario(input) {

    override val specification: Specification = specification {
        routing = routing {
            -validateOr binary {
                yes = route {
                    -computeSquare
                    -computePerimeter
                    -printResults
                }
                no = route {
                    -printError
                }
            }
        }
    }

    override val results = ArithmeticResults(
        perimeter = 0.0,
        square = 0.0,
        error = ""
    )

    companion object {
        val computePerimeter = Operation {
            val input = it.input as ArithmeticInput
            val results = it.results as ArithmeticResults

            val perimeter: Double = (input.a + input.b) * 2
            // this is one way to do it, not sure if it is the one preferred
            results.perimeter = perimeter
            //
            SINGLE_ROUTE result perimeter
        }

        private val computeSquare = Operation {
            val input = it.input as ArithmeticInput
            val results = it.results as ArithmeticResults

            val square: Double = (input.a * input.b)
            // this is one way to do it, not sure if it is the one preferred
            results.square = square
            //
            SINGLE_ROUTE result square
        }

        private val validateOr = Operation {
            val input = it.input as ArithmeticInput
            val results = it.results as ArithmeticResults

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
                results.error = e.message!!
                continueExecution = NO
            }
            continueExecution result None
        }

        private val printResults = Operation {
            val results = it.results as ArithmeticResults

            val square: Double = results.square
            val perimeter: Double = results.perimeter
            println("square = $square")
            println("perimeter = $perimeter")
            STOP_EXECUTION result None
        }

        private val printError = Operation {
            val results = it.results as ArithmeticResults

            val error: String = results.error
            println(error)
            STOP_EXECUTION result None
        }
    }
}
