package com.nexign.dsl.engine.worker

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.Scenario
import com.nexign.dsl.base.transitions.STOP_EXECUTION

class Worker {

    private lateinit var scenario: Scenario

    fun consume(scenario: Scenario) {
        this.scenario = scenario
    }

    fun startScenario() {
        var currentOp : Operation = Scenario.start
        try {
            while (currentOp != Scenario.end) {
                val condition = currentOp.run(scenario)
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
}
