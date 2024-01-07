package com.nexign.dsl.scenarios.examples.bpscenario

import com.nexign.dsl.base.*

class ExampleScenario(store: MutableMap<String, Any>) : Scenario(store)  {

    override val specification: Specification = specification (
            getAbonentInfo next checkAbonentActions binary {
                yes(prolongAction next notifyAboutActionTimePeriod next end)
                no(activateAction next writeOffMoney multiple {
                    +(YES to (cancelActionActivation next NotifyAction("error when activating action") next end))
                    +(NO to (NotifyAction("action activation") next notifyAboutActionTimePeriod))
                })
            }
    )

    companion object {
        val getAbonentInfo = GetAbonentInfo()
        val checkAbonentActions = CheckAbonentActions()
        val prolongAction = ProlongAction()
        val activateAction = ActivateAction()
        val writeOffMoney = WriteOffMoney()
        val cancelActionActivation = CancelActionActivation()

        val notifyAboutActionTimePeriod = NotifyAction("action time period")
        val end = object : Operation() {
            override val func: Scenario.() -> TransitionCondition = { STOP_EXECUTION }
        }
    }
}
