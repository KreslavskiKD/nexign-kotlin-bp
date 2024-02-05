package com.nexign.dsl.engine.worker

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.OperationDefault
import com.nexign.dsl.base.Scenario
import com.nexign.dsl.base.exceptions.NoSuchOperationException
import com.nexign.dsl.base.transitions.STOP_EXECUTION
import com.nexign.dsl.engine.logging.BasicWorkerLogger
import com.nexign.dsl.engine.logging.Logger
import com.nexign.dsl.engine.logging.RunStage
import com.nexign.dsl.engine.storage.Storage
import kotlin.reflect.full.primaryConstructor

class Worker {

    // These fields are not private because we need the `consume` function to be inlined
    lateinit var scenario: Scenario
    lateinit var params: Storage
    val logger: Logger = BasicWorkerLogger()

    inline fun <reified T: Scenario> consume(params: MutableMap<String, Any>) {
        val constructor = T::class.primaryConstructor
        if (constructor != null) {
            this.params = Storage(params, logger)
            this.scenario = constructor.call(this.params)
        }
    }

    fun startScenario() {
        var currentOp : Operation = Scenario.start
        try {
            logger.clear()
            routing(false, currentOp)
        } catch (e: Exception) {
            logger.log("Caught exception during routing: ${e.message}")
            if ((scenario.specification.errorRouting.isNotEmpty()) and (scenario.specification.errorRouting[currentOp] != null)) {
                routing(true, currentOp)
            } else if ((scenario.specification.errorRouting[currentOp] == null) and (scenario.specification.errorRouting[OperationDefault] != null)) {
                routing(true, OperationDefault)
            } else {
                logger.log("Had no error handling specified, throwing exception further.")
                throw e
            }
        }
    }

    fun getLastRun(): List<RunStage> = logger.getLog()

    private fun routing(error: Boolean, op: Operation) {
        var operation = if (error) {
            scenario.specification.errorRouting[op]!!
        } else {
            op
        }
        while (operation != Scenario.end) {
            if (error) {
                logger.log("error route: ${operation.javaClass.simpleName} started")
            } else {
                logger.log("${operation.javaClass.simpleName} started")
            }

            val condition = operation.run(scenario)

            if (error) {
                logger.log("error route: ${operation.javaClass.simpleName} ended with transition condition ${condition.javaClass.simpleName}")
            } else {
                logger.log("${operation.javaClass.simpleName} ended with transition condition ${condition.javaClass.simpleName}")
            }

            if (condition == STOP_EXECUTION) {
                break
            }
            val nextOp = scenario.specification.routing[operation]?.get(condition)
                ?: throw NoSuchOperationException()
            operation = nextOp
        }
    }

}
