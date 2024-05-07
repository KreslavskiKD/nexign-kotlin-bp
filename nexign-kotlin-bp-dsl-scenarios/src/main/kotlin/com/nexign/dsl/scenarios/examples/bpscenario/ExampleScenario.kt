package com.nexign.dsl.scenarios.examples.bpscenario

import com.nexign.dsl.base.*
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.scenario.data.DefaultResult
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.scenario.data.Results
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.specification.routing
import com.nexign.dsl.base.specification.specification
import com.nexign.dsl.base.transitions.*
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Subscriber
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Promotion
import kotlin.random.Random

data class ExampleScenarioInput(
    val subscriber: Subscriber,
    val promotion: Promotion,
) : Input

class ExampleScenario(override val input: ExampleScenarioInput) : Scenario(input)  {

    override val results: Results = DefaultResult()

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
        val specification: Specification = specification {
            routing = routing {
                -getSubscriberInfo
                -checkSubscriberPromotions binary {
                    yes = route {
                        -prolongPromotion
                        -notifyAboutPromotionTimePeriod
                        -end
                    }
                    no = route {
                        -activatePromotion
                        -writeOffMoney multiple {
                            +(YES to route {
                                -cancelPromotionActivation
                                -NotifyAction("error when activating promotion")
                                -end
                            })
                            +(NO to route {
                                -NotifyAction("promotion activation")
                                -notifyAboutPromotionTimePeriod
                            })
                        }
                    }
                }
            } errorRouting {
                listOf(activatePromotion, cancelPromotionActivation)  with ActionProblemsETC              togetherRoutesTo specialErrorHandling
                OperationDefault                                      with SomethingUnexpectedHappened    routesTo defaultErrorHandling
            }
        }

        private val getSubscriberInfo = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            println("getting abonent action ${input.subscriber.id}")
            SINGLE_ROUTE result None
        }

        private val prolongPromotion = Operation {
            // Do something
            SINGLE_ROUTE result None
        }

        private val activatePromotion = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            println("activating action ${input.promotion.name}")
            SINGLE_ROUTE result None
        }

        private val cancelPromotionActivation = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            println("cancelling action ${input.promotion.name} activation")
            SINGLE_ROUTE result None
        }

        private val checkSubscriberPromotions = Operation {
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

        private val notifyAboutPromotionTimePeriod = NotifyAction("action time period")
    }
}
