package com.nexign.dsl.engine.worker

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.STOP_EXECUTION
import com.nexign.dsl.base.Scenario

class Worker {

    private lateinit var scenario: Scenario

    fun consume(scenario: Scenario) {
        this.scenario = scenario
    }

    fun startScenario() {
        var currentOp : Operation = Scenario.start
        while (currentOp != Scenario.end) {
            val condition = currentOp.run(scenario)
            if (condition == STOP_EXECUTION) {
                break
            }
            val nextOp = scenario.specification[currentOp]?.get(condition)
                ?: throw IllegalStateException() // TODO: change to custom
            currentOp = nextOp
        }
        return
    }
}
