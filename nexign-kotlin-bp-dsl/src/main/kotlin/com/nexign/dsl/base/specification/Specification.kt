package com.nexign.dsl.base.specification

import com.nexign.dsl.base.description.ScenarioDescription

@DslMarker
annotation class SpecificationDSL

class Specification {

    @SpecificationDSL
    var routing : RoutingMap = RoutingMap()

    companion object {
        fun getDescription(scenarioName: String, specification: Specification) : ScenarioDescription {
            return specification.routing.getScenarioDescription(
                scenarioName = scenarioName,
                scenarioDetailedDescription = "" // TODO: here should be some logic to get details from e.g. KDoc
            )
        }
    }
}

fun specification(init: Specification.() -> Unit) : Specification {
    val spec = Specification()
    spec.init()
    return spec
}
