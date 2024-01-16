package com.nexign.dsl.scenarios.examples.arithmeticscenario

import com.nexign.dsl.base.Scenario
import com.nexign.dsl.base.Specification
import com.nexign.dsl.base.specification

class ArithmeticScenario(store: MutableMap<String, Any>) : Scenario(store) {

    override val specification: Specification = specification {
        start(ValidateOr() binary {
            yes(ComputeSquare() next ComputePerimeter() next PrintResults())
            no(PrintError())
        })
    }
}