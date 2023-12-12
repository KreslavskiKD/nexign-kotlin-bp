package com.nexign.dsl.drive

import com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario


fun main(args: Array<String>) {
    val scenario = ArithmeticScenario(
        mutableMapOf(
            "a" to 12.0,
            "b" to 5.5
        )
    )
    scenario.run {  }

    println(scenario.getScenarioDescription().toText())

    scenario.getLastRun().forEach {
        println(it.description)
    }
}
