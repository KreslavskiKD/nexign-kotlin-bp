package com.nexign.dsl.base.specification

import com.nexign.dsl.base.Operation

@DslMarker
annotation class SpecificationDSL

class Specification {

    @SpecificationDSL
    var routing : RoutingMap = RoutingMap()

    @SpecificationDSL
    var errorRouting: MutableMap<Operation, Operation> = mutableMapOf()

}

fun specification(init: Specification.() -> Unit) : Specification {
    val spec = Specification()
    spec.init()
    return spec
}
