package com.nexign.dsl.scenarios.examples.arithmeticscenario

import com.nexign.dsl.base.Scenario
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.specification.routing
import com.nexign.dsl.base.specification.specification

class ArithmeticScenario(store: MutableMap<String, Any>) : Scenario(store) {

    override val specification: Specification = specification {
        routing = routing {
            start(ValidateOr() binary {
                yes(ComputeSquare() next ComputePerimeter() next PrintResults())
                no(PrintError())
            })
        }
    }
}