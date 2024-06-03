package com.nexign.internship.dsl.base.scenario

import com.nexign.internship.dsl.base.None
import com.nexign.internship.dsl.base.Operation
import com.nexign.internship.dsl.base.OperationResult
import com.nexign.internship.dsl.base.result
import com.nexign.internship.dsl.base.scenario.data.Input
import com.nexign.internship.dsl.base.scenario.data.Results
import com.nexign.internship.dsl.base.transitions.SINGLE_ROUTE
import com.nexign.internship.dsl.base.transitions.START_EXECUTION
import com.nexign.internship.dsl.base.transitions.STOP_EXECUTION

abstract class Scenario: Operation {

    abstract val input: Input
    abstract val results: Results

    override fun run(scenario: Scenario): OperationResult {
        return SINGLE_ROUTE result results
    }

    companion object {

        val start: Operation = Operation {
            return@Operation START_EXECUTION result None
        }

        val end: Operation = Operation {
            return@Operation STOP_EXECUTION result None
        }
    }
}


