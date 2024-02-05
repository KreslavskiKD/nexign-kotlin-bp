package com.nexign.dsl.base.specification

import com.nexign.dsl.base.Operation
import com.nexign.dsl.base.transitions.ErrorTransitionCondition


@SpecificationDSL
class ErrorRoutingMap {

    private val errorRoutingMap = HashMap<Operation, MutableMap<ErrorTransitionCondition, Operation>>()

    operator fun get(operation: Operation): MutableMap<ErrorTransitionCondition, Operation>? {
        if (errorRoutingMap[operation] == null) {
            return null
        }
        return errorRoutingMap[operation] as MutableMap<ErrorTransitionCondition, Operation>
    }

    fun isNotEmpty(): Boolean = errorRoutingMap.isNotEmpty()

    infix fun <A : Pair<Operation, ErrorTransitionCondition>, B : Operation> A.routesTo(errorHandlingOperation: B) {
        if (errorRoutingMap[this.first] != null) {
            errorRoutingMap[this.first]?.set(this.second, errorHandlingOperation)
        } else {
            errorRoutingMap[this.first] = mutableMapOf(this.second to errorHandlingOperation)
        }
    }

    infix fun <A : Pair<List<Operation>, ErrorTransitionCondition>, B : Operation> A.routesTo(errorHandlingOperation: B) {
        for (op : Operation in this.first) {
            if (errorRoutingMap[op] != null) {
                errorRoutingMap[op]?.set(this.second, errorHandlingOperation)
            } else {
                errorRoutingMap[op] = mutableMapOf(this.second to errorHandlingOperation)
            }
        }
    }

    infix fun <A, B : ErrorTransitionCondition> A.with(that: B): Pair<A, B> = Pair(this, that)
}

fun errorRouting(init: ErrorRoutingMap.() -> Unit) : ErrorRoutingMap {
    val spec = ErrorRoutingMap()
    spec.init()
    return spec
}

