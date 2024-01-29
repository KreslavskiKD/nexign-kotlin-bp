package com.nexign.dsl.engine.worker

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.Scenario
import com.nexign.dsl.base.transitions.STOP_EXECUTION
import com.nexign.dsl.engine.logging.BasicWorkerLogger
import com.nexign.dsl.engine.logging.Logger
import com.nexign.dsl.engine.logging.RunStage
import com.nexign.dsl.engine.storage.Storage
import kotlin.reflect.full.primaryConstructor

class Worker {

    // These fields are not private because we need the consume function to be inlined
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
            while (currentOp != Scenario.end) {
                logger.log("${currentOp.javaClass.simpleName} started")
                val condition = currentOp.run(scenario)
                logger.log("${currentOp.javaClass.simpleName} ended with transition condition ${condition.javaClass.simpleName}")
                if (condition == STOP_EXECUTION) {
                    break
                }
                val nextOp = scenario.specification.routing[currentOp]?.get(condition)
                    ?: throw IllegalStateException() // TODO: change to custom
                currentOp = nextOp
            }
        } catch (e: Exception) {
            if ((scenario.specification.errorRouting.isNotEmpty()) and (scenario.specification.errorRouting[currentOp] != null)) {
                var errorOp = scenario.specification.errorRouting[currentOp]!!
                while (errorOp != Scenario.end) {
                    val condition = errorOp.run(scenario)
                    if (condition == STOP_EXECUTION) {
                        break
                    }
                    val nextOp = scenario.specification.routing[errorOp]?.get(condition)
                        ?: throw IllegalStateException() // TODO: change to custom
                    errorOp = nextOp
                }
            } else {
                throw e // TODO: may be here should be a logger, or the logger should either be at the Engine itself
            }
        }
    }

    fun getLastRun(): List<RunStage> = logger.getLog()

}
