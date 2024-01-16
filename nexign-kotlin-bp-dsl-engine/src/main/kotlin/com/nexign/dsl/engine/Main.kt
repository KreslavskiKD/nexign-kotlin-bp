package com.nexign.dsl.engine

import com.nexign.dsl.engine.worker.Worker
import com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario
import com.nexign.dsl.scenarios.examples.bpscenario.ExampleScenario
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Abonent
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Action


fun main(args: Array<String>) {
    val worker = Worker()

    val scenario = ArithmeticScenario(
        mutableMapOf(
            "a" to 12.0,
            "b" to 5.5,
        )
    )

    worker.consume(scenario)
    worker.startScenario()

    println(scenario.getDescription().toText())

    // TODO: move last run saving to engine
    scenario.getLastRun().forEach {
        println(it.description)
    }

    val bpScenario = ExampleScenario(
        mutableMapOf(
            "abonent" to Abonent("erf156-15edyu-98wer7"),
            "action" to Action("quiz"),
        )
    )

    println("\n\n")
    println(bpScenario.getDescription().toText())

    worker.consume(bpScenario)
    worker.startScenario()
}
