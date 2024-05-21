package com.nexign.dsl.scenarios.examples.bpscenario

import com.nexign.dsl.base.*
import com.nexign.dsl.base.scenario.Scenario
import com.nexign.dsl.base.scenario.data.DefaultResult
import com.nexign.dsl.base.scenario.data.Input
import com.nexign.dsl.base.scenario.data.Results
import com.nexign.dsl.base.specification.Specifiable
import com.nexign.dsl.base.specification.Specification
import com.nexign.dsl.base.specification.route
import com.nexign.dsl.base.transitions.*
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Subscriber
import com.nexign.dsl.scenarios.examples.bpscenario.mock.Promotion
import kotlin.random.Random

data class ExampleScenarioInput(
    val subscriber: Subscriber,
    val promotion: Promotion,
) : Input

class ExampleScenario(override val input: ExampleScenarioInput) : Scenario() {

    override val results: Results = DefaultResult()

    companion object: Specifiable {
        override fun specification(): Specification = route {
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
                            -notifyAboutErrorWithPromotionActivation
                            -end
                        })
                        +(NO to route {
                            -notifyAboutPromotionActivation
                            -notifyAboutPromotionTimePeriod
                            -end
                        })
                    }
                }
            }
        } errorRouting {
            listOf(activatePromotion, cancelPromotionActivation)  with ActionProblemsETC              togetherRoutesTo specialErrorHandling
            OperationDefault                                      with SomethingUnexpectedHappened    routesTo defaultErrorHandling
        }

        private val getSubscriberInfo = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            // println("getting abonent action ${input.subscriber.id}")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val prolongPromotion = Operation {
            // Do something
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val activatePromotion = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            // println("activating action ${input.promotion.name}")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val cancelPromotionActivation = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            // println("cancelling action ${input.promotion.name} activation")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val checkSubscriberPromotions = Operation {
            var transitionCondition: TransitionCondition = YES

            // decision imitation
            val isActionAlreadyActive: Boolean = Random.nextBoolean()
            // Do something
            Thread.sleep(1000)
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
            Thread.sleep(1000)
            if (isThereEnoughMoney) {
                transitionCondition = NO
            }
            transitionCondition result None
        }

        private val defaultErrorHandling = Operation {
            val error: String = it.results.error
            println(error)
            Thread.sleep(1000)
            STOP_EXECUTION result None
        }

        private val specialErrorHandling = Operation {
            val error: String = it.results.error
            println("Some super error handling: $error")
            Thread.sleep(1000)
            STOP_EXECUTION result None
        }

        private val notifyAboutPromotionTimePeriod = Operation {
            // println("Notifying about promotion time period")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val notifyAboutPromotionActivation = Operation {
            // println("Notifying about promotion activation")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val notifyAboutErrorWithPromotionActivation = Operation {
            // println("Notifying about error with promotion activation")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }
    }
}
