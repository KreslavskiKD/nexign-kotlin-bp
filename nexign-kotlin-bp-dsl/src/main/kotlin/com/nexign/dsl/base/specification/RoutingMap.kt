package com.nexign.dsl.base.specification

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.Scenario
import com.nexign.dsl.base.description.OperationDescription
import com.nexign.dsl.base.description.ScenarioDescription
import com.nexign.dsl.base.transitions.*


@SpecificationDSL
class RoutingMap {
    private val specification = HashMap<Operation, MutableMap<TransitionCondition, Operation>>()

    operator fun get(operation: Operation): Map<TransitionCondition, Operation>? {
        if (specification[operation] == null) {
            return null
        }
        return specification[operation] as Map<TransitionCondition, Operation>
    }

    infix fun Operation.next(operation: Operation) : Operation {
        val lastOp = this@next.getLastOperationInRow()

        if (specification[lastOp] == null) {
            specification[lastOp] = mutableMapOf()
        }

        specification[lastOp]?.put(SINGLE_ROUTE, operation)
        return this@next
    }

    infix fun Operation.binary(init: BinaryChoice.() -> Unit) : Operation {
        val binaryChoice = BinaryChoice()
        binaryChoice.init()

        val lastOp = this@binary.getLastOperationInRow()

        if (specification[lastOp] == null) {
            specification[lastOp] = mutableMapOf()
        }

        specification[lastOp]?.put(YES, binaryChoice.yesOperation)
        specification[lastOp]?.put(NO, binaryChoice.noOperation)
        return this@binary
    }

    infix fun Operation.multiple(init: MultipleChoiceBuilder.() -> Unit) : Operation {
        val mc = MultipleChoiceBuilder()
        mc.init()

        val lastOp = this@multiple.getLastOperationInRow()

        if (specification[lastOp] == null) {
            specification[lastOp] = mutableMapOf()
        }

        for (p in mc.choices) {
            specification[lastOp]?.put(p.first, p.second)
        }
        return this@multiple
    }

    fun start(operation: Operation) {
        specification[Scenario.start] = mutableMapOf(
            START_EXECUTION to operation
        )
    }

    fun getScenarioDescription(scenarioName: String, scenarioDetailedDescription: String): ScenarioDescription {
        val startingOperation = Scenario.start

        val visited: MutableSet<Operation> = mutableSetOf()
        val opsDescrs: MutableMap<Operation, OperationDescription> = mutableMapOf()

        var currentOps = listOf(startingOperation)
        var nextOps = mutableListOf<Operation>()

        opsDescrs[startingOperation] = startingOperation.getOperationDescription()

        while (currentOps.isNotEmpty()) {
            for (op in currentOps) {
                if (specification[op] != null) {
                    for (nop in specification[op]!!) {
                        if (!visited.contains(nop.value)) {
                            opsDescrs[nop.value] = nop.value.getOperationDescription()
                            visited.add(nop.value)
                            nextOps.add(nop.value)
                        }

                        opsDescrs[op]?.transitions?.set(nop.key, opsDescrs[nop.value]!!)
                    }
                }

            }
            currentOps = nextOps
            nextOps = mutableListOf()
        }

        return ScenarioDescription(
            scenarioName = scenarioName,
            startingOperation = opsDescrs[startingOperation]!!,
            detailedDescription = scenarioDetailedDescription,
        )
    }

    private fun Operation.getLastOperationInRow() : Operation {
        return if (specification[this@getLastOperationInRow] == null) {
            this@getLastOperationInRow
        } else if (specification[this@getLastOperationInRow]?.get(SINGLE_ROUTE) == null) {
            this@getLastOperationInRow
        } else {
            var curLastOp = specification[this@getLastOperationInRow]?.get(SINGLE_ROUTE)
            var nextOp = specification[curLastOp]?.get(SINGLE_ROUTE)

            while (nextOp != null) {
                curLastOp = nextOp
                nextOp = specification[curLastOp]?.get(SINGLE_ROUTE)
            }

            curLastOp!!
        }
    }
}

fun routing(init: RoutingMap.() -> Unit) : RoutingMap {
    val spec = RoutingMap()
    spec.init()
    return spec
}


class BinaryChoice {
    lateinit var yesOperation: Operation
    lateinit var noOperation: Operation

    fun yes(op: Operation) {
        yesOperation = op
    }

    fun no(op: Operation) {
        noOperation = op
    }
}

class MultipleChoiceBuilder {
    val choices = mutableListOf<Pair<TransitionCondition, Operation>>()

    operator fun Pair<TransitionCondition, Operation>.unaryPlus() {
        choices += this
    }

    operator fun Pair<Int, Operation>.unaryMinus() {
        choices += Pair(NumberedTCMap.getNumberedTC(this.first), this.second)
    }
}

