package com.nexign.dsl.engine.worker

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.OperationDefault
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.exceptions.NexignBpNoSuchOperationException
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.specification.Specifiable
import com.nexign.dsl.base.transitions.ErrorTransitionCondition
import com.nexign.dsl.base.transitions.STOP_EXECUTION
import com.nexign.dsl.base.transitions.SomethingUnexpectedHappened
import com.nexign.dsl.engine.logging.BasicWorkerLogger
import com.nexign.dsl.engine.logging.Logger
import com.nexign.dsl.engine.logging.RunStage
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class Worker {

    private lateinit var scenario: Scenario
    private lateinit var clazz: KClass<out Scenario>
    private val logger: Logger = BasicWorkerLogger()

    fun consume(input: Input, clazz: KClass<out Scenario>) {
        val constructor = clazz.primaryConstructor

        if (constructor != null) {
            this.scenario = constructor.call(input)
            this.clazz = clazz
        }
    }

    suspend fun startScenario() {
        var currentOp : Operation = Scenario.start
        val specification = (clazz.java.cast(scenario) as Specifiable).specification()

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

                val nextOp: Operation = specification.routing[currentOp]?.get(condition)
                        ?: throw NexignBpNoSuchOperationException("No operation from $currentOp in case of condition $condition")

                currentOp = nextOp
            }
        } catch (e: Exception) {
            if (specification.routing[OperationDefault] != null && specification.routing[OperationDefault]?.isNotEmpty() == true) {
                val errorHandlingOp = specification.routing[OperationDefault]?.get(SomethingUnexpectedHappened)
                    ?: throw e

                logger.log("default error handling route:\n exception happened:\n${e.message}\nstarted default handling with operation ${errorHandlingOp.javaClass.simpleName}")
                val results = errorHandlingOp.run(scenario) // TODO: what should we do with the results here?
                return
            }


            logger.log("Caught exception during routing: ${e.message}")
            throw e
        }
    }

    fun getLastRun(): List<RunStage> = logger.getLog()

}
