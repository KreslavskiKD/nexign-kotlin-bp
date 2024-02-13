package com.nexign.dsl.engine.worker

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.exceptions.NoSuchOperationException
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.transitions.ErrorTransitionCondition
import com.nexign.dsl.base.transitions.STOP_EXECUTION
import com.nexign.dsl.engine.logging.BasicWorkerLogger
import com.nexign.dsl.engine.logging.Logger
import com.nexign.dsl.engine.logging.RunStage
import kotlin.reflect.full.primaryConstructor

class Worker {

    // These fields are not private because we need the `consume` function to be inlined
    lateinit var scenario: Scenario
    val logger: Logger = BasicWorkerLogger()

    inline fun <reified T: Scenario> consume(input: Input) {
        val constructor = T::class.primaryConstructor
        if (constructor != null) {
            this.scenario = constructor.call(input)
        }
    }

    fun startScenario() {
        var currentOp : Operation = Scenario.start
        try {
            logger.clear()
            var error = false
            while (currentOp != Scenario.end) {
                if (error) {
                    logger.log("error route: ${currentOp.getOperationName()} started")
                } else {
                    logger.log("${currentOp.getOperationName()} started")
                }

                val results = currentOp.run(scenario) // TODO: what should we do with the results here?
                val condition = results.transitionCondition

                error = when (condition) {
                    is ErrorTransitionCondition -> true
                    else -> false
                }

                if (error) {
                    logger.log("error handling route: ${currentOp.getOperationName()} ended with transition condition ${condition.javaClass.simpleName}")
                } else {
                    logger.log("${currentOp.getOperationName()} ended with transition condition ${condition.javaClass.simpleName}")
                }

                if (condition == STOP_EXECUTION) {
                    break
                }

                val nextOp = scenario.specification.routing[currentOp]?.get(condition)
                        ?: throw NoSuchOperationException()

                currentOp = nextOp
            }
        } catch (e: Exception) {
            logger.log("Caught exception during routing: ${e.message}")
            throw e
        }
    }

    fun getLastRun(): List<RunStage> = logger.getLog()

}
