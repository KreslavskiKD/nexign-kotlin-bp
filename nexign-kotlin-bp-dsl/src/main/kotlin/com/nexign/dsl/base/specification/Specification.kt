package com.nexign.dsl.base.specification

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.description.OperationDescription
import com.nexign.dsl.base.description.ScenarioDescription
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.transitions.*

@DslMarker
annotation class SpecificationDSL

class Specification {

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

    infix fun errorRouting(init: ErrorRoutingBuilder.() -> Unit): Specification {
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

    class ErrorRoutingBuilder(private val routing: Specification) {

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

    class RoutingBlockBuilder(private val spec: Specification) {
        val route = mutableListOf<Operation>()

        operator fun Operation.unaryMinus(): Operation {
            route += this@unaryMinus
            return this@unaryMinus
        }

        infix fun Operation.binary(init: BinaryChoice.() -> Unit) : Operation {
            val binaryChoice = BinaryChoice(spec)
            binaryChoice.init()
            binaryChoice.build()

            if (spec.specification[this@binary] == null) {
                spec.specification[this@binary] = mutableMapOf()
            }

            spec.specification[this@binary]?.put(YES, binaryChoice.yes.route[0])
            spec.specification[this@binary]?.put(NO, binaryChoice.no.route[0])
            return this@binary
        }

        infix fun Operation.multiple(init: MultipleChoiceBuilder.() -> Unit) : Operation {
            val mc = MultipleChoiceBuilder(spec)
            mc.init()

            if (spec.specification[this@multiple] == null) {
                spec.specification[this@multiple] = mutableMapOf()
            }

            for (p in mc.choices) {
                spec.specification[this@multiple]?.put(p.first, p.second)
            }
            return this@multiple
        }
    }

    class BinaryChoice(private val spec: Specification) {
        lateinit var yes: RoutingBlockBuilder
        lateinit var no: RoutingBlockBuilder

        fun build() {
            spec.buildBlock(yes)
            spec.buildBlock(no)
        }

        fun route(init: RoutingBlockBuilder.() -> Unit) : RoutingBlockBuilder
                = RoutingBlockBuilder(spec).apply(init)
    }

    class MultipleChoiceBuilder(private val spec: Specification) {
        val choices = mutableListOf<Pair<TransitionCondition, Operation>>()

        operator fun Pair<TransitionCondition, RoutingBlockBuilder>.unaryPlus() {
            spec.buildBlock(this.second)
            choices += Pair(this.first, this.second.route[0])
        }
    }

    companion object {
        fun getDescription(scenarioName: String, specification: Specification) : ScenarioDescription {
            return specification.getScenarioDescription(
                scenarioName = scenarioName,
                scenarioDetailedDescription = "" // TODO: here should be some logic to get details from e.g. KDoc
            )
        }
    }
}

fun route(init: Specification.RoutingBlockBuilder.() -> Unit) : Specification {
    val rmap = Specification()
    val block = Specification.RoutingBlockBuilder(rmap)
    block.init()
    rmap.buildBlock(block)
    return rmap
}
