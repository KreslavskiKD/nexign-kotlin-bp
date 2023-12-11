package com.nexign.dsl.base

@DslMarker
annotation class SpecificationDSL

@SpecificationDSL
class Specification : HashMap<TransitionCondition, Operation>() {

    fun setTransition(tc: TransitionCondition, operation: Operation) {
        this[tc] = operation
    }

    fun start(operation: Operation) {
        this[START_EXECUTION] = operation
    }
}

fun initCustomSpecification(init: Specification.() -> Unit) : Specification {
    val spec = Specification()
    spec.init()
    return spec
}

fun specification(operation: Operation) : Specification {
    val spec = Specification()
    spec.start(operation)
    return spec
}
