package com.nexign.dsl.base.specification

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.scenario.Scenario
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

    private fun start(operation: Operation) {
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

    infix fun errorRouting(init: ErrorRoutingBuilder.() -> Unit): RoutingMap {
        val erb = ErrorRoutingBuilder(this)
        erb.init()
        return this
    }

    fun buildBlock(block: RoutingBlockBuilder, operation: Operation = Scenario.start) {
        if (operation == Scenario.start) {
            this.start(block.route[0])
        } else {
            if (specification[operation] != null) {
                specification[operation]?.set(SINGLE_ROUTE, block.route[0])
            } else {
                specification[operation] = mutableMapOf(SINGLE_ROUTE to operation)
            }
        }

        var prev = block.route[0]
        for (i in 1 until block.route.size) {
            val cur = block.route[i]
            if (specification[prev] != null) {
                specification[prev]?.set(SINGLE_ROUTE, cur)
            } else {
                specification[prev] = mutableMapOf(SINGLE_ROUTE to cur)
            }
            prev = cur
        }
    }

    class ErrorRoutingBuilder(private val routing: RoutingMap) {

        infix fun <A : Pair<Operation, ErrorTransitionCondition>, B : Operation> A.routesTo(errorHandlingOperation: B) {
            if (routing.specification[this.first] != null) {
                routing.specification[this.first]?.set(this.second, errorHandlingOperation)
            } else {
                routing.specification[this.first] = mutableMapOf(this.second to errorHandlingOperation)
            }
        }

        infix fun <A : Pair<List<Operation>, ErrorTransitionCondition>, B : Operation> A.togetherRoutesTo(errorHandlingOperation: B) {
            for (op : Operation in this.first) {
                if (routing.specification[op] != null) {
                    routing.specification[op]?.set(this.second, errorHandlingOperation)
                } else {
                    routing.specification[op] = mutableMapOf(this.second to errorHandlingOperation)
                }
            }
        }

        infix fun <A, B : ErrorTransitionCondition> A.with(that: B): Pair<A, B> = Pair(this, that)
    }

    class RoutingBlockBuilder(private val routingMap: RoutingMap) {
        val route = mutableListOf<Operation>()

        operator fun Operation.unaryMinus(): Operation {
            route += this
            return this
        }

        infix fun Operation.binary(init: BinaryChoice.() -> Unit) : Operation {
            val binaryChoice = BinaryChoice(routingMap)
            binaryChoice.init()
            binaryChoice.build()

            if (routingMap.specification[this] == null) {
                routingMap.specification[this] = mutableMapOf()
            }

            routingMap.specification[this]?.put(YES, binaryChoice.yes.route[0])
            routingMap.specification[this]?.put(NO, binaryChoice.no.route[0])
            return this@binary
        }

        infix fun Operation.multiple(init: MultipleChoiceBuilder.() -> Unit) : Operation {
            val mc = MultipleChoiceBuilder(routingMap)
            mc.init()

            if (routingMap.specification[this] == null) {
                routingMap.specification[this] = mutableMapOf()
            }

            for (p in mc.choices) {
                routingMap.specification[this]?.put(p.first, p.second)
            }
            return this@multiple
        }
    }

    class BinaryChoice(private val routingMap: RoutingMap) {
        lateinit var yes: RoutingBlockBuilder
        lateinit var no: RoutingBlockBuilder

        fun build() {
            routingMap.buildBlock(yes)
            routingMap.buildBlock(no)
        }

        fun route(init: RoutingBlockBuilder.() -> Unit) : RoutingBlockBuilder
                = RoutingBlockBuilder(routingMap).apply(init)
    }

    class MultipleChoiceBuilder(private val routingMap: RoutingMap) {
        val choices = mutableListOf<Pair<TransitionCondition, Operation>>()

        operator fun Pair<TransitionCondition, RoutingBlockBuilder>.unaryPlus() {
            routingMap.buildBlock(this.second)
            choices += Pair(this.first, this.second.route[0])
        }
    }

}

fun routing(init: RoutingMap.RoutingBlockBuilder.() -> Unit) : RoutingMap {
    val rmap = RoutingMap()
    val block = RoutingMap.RoutingBlockBuilder(rmap).apply(init)
    rmap.buildBlock(block)
    return rmap
}
