package com.nexign.dsl.scenarios.examples.bpscenario

import com.nexign.dsl.base.*

class ExampleScenario(store: MutableMap<String, Any>) : Scenario(store)  {

    override val specification: Specification = specification (
            GetAbonentInfo() next CheckAbonentActions() binary {
                yes(ProlongAction() next notifyAboutActionTimePeriod next end)
                no(ActivateAction() next WriteOffMoney() multiple {
                    +(YES to (CancelActionActivation() next NotifyAction("error when activating action") next end))
                    +(NO to (NotifyAction("action activation") next notifyAboutActionTimePeriod))
                })
            }
    )


    companion object {

        // May be should be a global constant
        val end = object : Operation() {
            override val func: Scenario.() -> TransitionCondition = { STOP_EXECUTION }
        }

        val notifyAboutActionTimePeriod = NotifyAction("action time period") next end
    }
}
