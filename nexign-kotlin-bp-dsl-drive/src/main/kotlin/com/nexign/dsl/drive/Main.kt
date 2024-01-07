package com.nexign.dsl.drive

import com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario
import com.nexign.dsl.scenarios.examples.bpscenario.ExampleScenario
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Abonent
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Action


fun main(args: Array<String>) {
    val scenario = ArithmeticScenario(
        mutableMapOf(
            "a" to 12.0,
            "b" to 5.5,
        )
    )
    scenario.run {  }

    println(scenario.getScenarioDescription().toText())

    scenario.getLastRun().forEach {
        println(it.description)
    }

    val bpScenario = ExampleScenario(
        mutableMapOf(
            "abonent" to Abonent("erf156-15edyu-98wer7"),
            "action" to Action("quiz"),
        )
    )
    bpScenario.run {  }
}
