package com.nexign.dsl.base.specification

@DslMarker
annotation class SpecificationDSL

class Specification {

    @SpecificationDSL
    var routing : RoutingMap = RoutingMap()
}

fun specification(init: Specification.() -> Unit) : Specification {
    val spec = Specification()
    spec.init()
    return spec
}
