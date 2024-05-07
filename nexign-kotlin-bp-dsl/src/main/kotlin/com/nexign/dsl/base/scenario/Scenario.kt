package com.nexign.dsl.base.scenario

import com.nexign.dsl.base.None
import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.OperationResult
import com.nexign.dsl.base.description.ScenarioDescription
import com.nexign.dsl.base.result
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.scenario.data.Results
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.transitions.SINGLE_ROUTE
import com.nexign.dsl.base.transitions.START_EXECUTION
import com.nexign.dsl.base.transitions.STOP_EXECUTION

abstract class Scenario(open val input: Input): Operation {

    abstract val results: Results

    override fun run(scenario: Scenario): OperationResult {
        return SINGLE_ROUTE result results
    }

    fun getSpecification(): Specification = specification

    companion object {
        val specification : Specification = Specification()

        fun getDescription(scenarioName: String, specification: Specification = this.specification) : ScenarioDescription {
            return specification.routing.getScenarioDescription(
                scenarioName = scenarioName,
                scenarioDetailedDescription = "" // TODO: here should be some logic to get details from e.g. KDoc
            )
        }

        val start: Operation = Operation {
            return@Operation START_EXECUTION result None
        }

        val end: Operation = Operation {
            return@Operation STOP_EXECUTION result None
        }
    }
}


