package com.nexign.dsl.base

import com.nexign.dsl.base.description.OperationDescription

open class Operation {
    open val specification : Specification = Specification()
    protected open val func : Scenario.() -> TransitionCondition = { TransitionCondition() }

    fun start(scenario: Scenario) {
        val condition = scenario.func()
        if (condition is STOP_EXECUTION) {
            return
        }
        if (specification[condition] != null) {
            specification[condition]!!.start(scenario)
        } else {
            throw IllegalArgumentException() // TODO change to custom
        }
        return
    }

    fun getOperationDescription() : OperationDescription =
        OperationDescription(
            operationName = this.javaClass.simpleName,
            transitions = linkedMapOf(),
            detailedDescription = "",
        )

    infix fun next(op: Operation) : Operation {
        val lastOp = getLastOperationInRow()
        lastOp.specification[SINGLE_ROUTE] = op
        return this
    }

    infix fun binary(init: BinaryChoice.() -> Unit) : Operation {
        val binaryChoice = BinaryChoice()
        binaryChoice.init()
        val lastOp = getLastOperationInRow()
        lastOp.specification[YES] = binaryChoice.yesOperation
        lastOp.specification[NO] = binaryChoice.noOperation
        return this
    }

    infix fun multiple(init: MultipleChoiceBuilder.() -> Unit) : Operation {
        val mc = MultipleChoiceBuilder()
        mc.init()
        val lastOp = getLastOperationInRow()
        for (p in mc.choices) {
            lastOp.specification[p.first] = p.second
        }
        return this
    }

    inline fun <reified T : Any> Scenario.getFromStorage(name: String) : T {
        return this@getFromStorage.getFromStorage(name, classOpFromStackTraces())
    }

    inline fun <reified T : Any> Scenario.putInStorage(name: String, value: T) {
        this@putInStorage.putInStorage(name, value, classOpFromStackTraces())
    }

    private fun getLastOperationInRow(): Operation {
        return if (specification[SINGLE_ROUTE] == null) {
            this
        } else {
            var curLastOp = specification[SINGLE_ROUTE]
            var nextOp = curLastOp?.specification?.get(SINGLE_ROUTE)

            while (nextOp != null) {
                curLastOp = nextOp
                nextOp = curLastOp.specification[SINGLE_ROUTE]
            }

            curLastOp!!
        }
    }
}

inline fun Scenario.checkIn() {
    this@checkIn.operationCheckIn("${classOpFromStackTraces()} started")
}

inline fun Scenario.checkOut(transitionCondition: TransitionCondition) {
    this@checkOut.operationCheckIn("${classOpFromStackTraces()} ended with transition condition ${transitionCondition.javaClass.simpleName}")
}

fun operation(init: Operation.() -> Unit) : Operation {
    val operation = Operation()
    operation.init()
    return operation
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

inline infix fun Scenario.functionBuilder(innerFunc: Scenario.() -> TransitionCondition) : TransitionCondition {
    this.checkIn()
    val tc = this.innerFunc()
    this.checkOut(tc)
    return tc
}