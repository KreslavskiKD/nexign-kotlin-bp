package com.nexign.dsl.base.specification

import com.nexign.dsl.base.Operation


@SpecificationDSL
class ErrorRoutingMap {

    private val errorRoutingMap = HashMap<Operation, Operation>()

    operator fun get(operation: Operation): Operation? {
        if (errorRoutingMap[operation] == null) {
            return null
        }
        return errorRoutingMap[operation] as Operation
    }

    fun isNotEmpty(): Boolean = errorRoutingMap.isNotEmpty()

    infix fun <A : Operation, B : Operation> A.routesTo(errorHandlingOperation: B) {
        errorRoutingMap[this] = errorHandlingOperation
    }

    infix fun <A : List<Operation>, B : Operation> A.routesTo(errorHandlingOperation: B) {
        for (op : Operation in this) {
            errorRoutingMap[op] = errorHandlingOperation
        }
    }
}

fun errorRouting(init: ErrorRoutingMap.() -> Unit) : ErrorRoutingMap {
    val spec = ErrorRoutingMap()
    spec.init()
    return spec
}
