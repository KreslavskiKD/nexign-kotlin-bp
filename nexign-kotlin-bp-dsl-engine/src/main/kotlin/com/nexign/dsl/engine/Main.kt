package com.nexign.dsl.engine

import com.nexign.dsl.engine.worker.Worker
import com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput
import com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario
import com.nexign.dsl.scenarios.examples.bpscenario.ExampleScenario
import com.nexign.dsl.scenarios.examples.bpscenario.ExampleScenarioInput
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Abonent
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Action


fun main(args: Array<String>) {
    val worker = Worker()

    worker.consume<ArithmeticScenario>(ArithmeticInput(
        a = 12.0,
        b = 5.5
    ))
    worker.startScenario()

    println(worker.scenario.getDescription().toText())

    worker.getLastRun().forEach {
        println(it.description)
    }

    worker.consume<ExampleScenario>(ExampleScenarioInput(
        abonent = Abonent("erf156-15edyu-98wer7"),
        action = Action("quiz"),
    ))
    println("\n\n")
    println(worker.scenario.getDescription().toText())

    worker.startScenario()
}
