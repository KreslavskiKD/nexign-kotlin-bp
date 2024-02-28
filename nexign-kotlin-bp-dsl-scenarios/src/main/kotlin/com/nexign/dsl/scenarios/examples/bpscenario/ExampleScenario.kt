package com.nexign.dsl.scenarios.examples.bpscenario

import com.nexign.dsl.base.*
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.specification.routing
import com.nexign.dsl.base.specification.specification
import com.nexign.dsl.base.transitions.*
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Abonent
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Action
import kotlin.random.Random

data class ExampleScenarioInput(
    val abonent: Abonent,
    val action: Action,
) : Input

class ExampleScenario(override val input: ExampleScenarioInput) : Scenario(input)  {

    override val specification: Specification = specification {
        routing = routing {
            -getAbonentInfo
            -checkAbonentActions binary {
                yes = route {
                    -prolongAction
                    -notifyAboutActionTimePeriod
                    -end
                }
                no = route {
                    -activateAction
                    -writeOffMoney multiple {
                        +(YES to route {
                            -cancelActionActivation
                            -NotifyAction("error when activating action")
                            -end
                        })
                        +(NO to route {
                            -NotifyAction("action activation")
                            -notifyAboutActionTimePeriod
                        })
                    }
                }
            }
        } errorRouting {
            listOf(activateAction, cancelActionActivation)  with ActionProblemsETC              togetherRoutesTo specialErrorHandling
            OperationDefault                                with SomethingUnexpectedHappened    routesTo defaultErrorHandling
        }
    }

    open class NotifyAction(
        private val message: String,
    ) : Operation {

        override fun run(scenario: Scenario): OperationResult {
            // Do something
            println("I am notified about $message")

            return SINGLE_ROUTE result None
        }
    }

    companion object {
        private val getAbonentInfo = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            println("getting abonent action ${input.abonent.id}")
            SINGLE_ROUTE result None
        }

        private val prolongAction = Operation {
            // Do something
            SINGLE_ROUTE result None
        }

        private val activateAction = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            println("activating action ${input.action.name}")
            SINGLE_ROUTE result None
        }

        private val cancelActionActivation = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            println("cancelling action ${input.action.name} activation")
            SINGLE_ROUTE result None
        }

        private val checkAbonentActions = Operation {
            var transitionCondition: TransitionCondition = YES

            // decision imitation
            val isActionAlreadyActive: Boolean = Random.nextBoolean()
            // Do something
            if (isActionAlreadyActive) {
                transitionCondition = NO
            }
            transitionCondition result None
        }

        private val writeOffMoney = Operation {
            var transitionCondition: TransitionCondition = YES

            // decision imitation
            val isThereEnoughMoney: Boolean = Random.nextBoolean()
            // Do something
            if (isThereEnoughMoney) {
                transitionCondition = NO
            }
            transitionCondition result None
        }

        private val defaultErrorHandling = Operation {
            val error: String = it.results.error
            println(error)
            STOP_EXECUTION result None
        }

        private val specialErrorHandling = Operation {
            val error: String = it.results.error
            println("Some super error handling: $error")
            STOP_EXECUTION result None
        }

        private val notifyAboutActionTimePeriod = NotifyAction("action time period")
    }

}
