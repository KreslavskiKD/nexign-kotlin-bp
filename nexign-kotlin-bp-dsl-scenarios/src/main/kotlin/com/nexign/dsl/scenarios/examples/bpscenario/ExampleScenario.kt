package com.nexign.dsl.scenarios.examples.bpscenario

import com.nexign.dsl.base.*
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.specification.routing
import com.nexign.dsl.base.specification.specification
import com.nexign.dsl.base.transitions.NO
import com.nexign.dsl.base.transitions.YES

class ExampleScenario(store: MutableMap<String, Any>) : Scenario(store)  {

    override val specification: Specification = specification {
        routing = routing {
            start(getAbonentInfo next checkAbonentActions binary {
                yes(prolongAction next notifyAboutActionTimePeriod next end)
                no(activateAction next writeOffMoney multiple {
                    +(YES to (cancelActionActivation next NotifyAction("error when activating action") next end))
                    +(NO to (NotifyAction("action activation") next notifyAboutActionTimePeriod))
                })
            })
        }

        errorRouting = mutableMapOf(
            activateAction to specialErrorHandling,
            OperationDefault to defaultErrorHandling,
            cancelActionActivation to specialErrorHandling,
        )
    }

    companion object {
        val getAbonentInfo = GetAbonentInfo()
        val checkAbonentActions = CheckAbonentActions()
        val prolongAction = ProlongAction()
        val activateAction = ActivateAction()
        val writeOffMoney = WriteOffMoney()
        val cancelActionActivation = CancelActionActivation()

        val specialErrorHandling = SpecialErrorHandling()
        val defaultErrorHandling = DefaultErrorHandling()

        val notifyAboutActionTimePeriod = NotifyAction("action time period")
    }
}
