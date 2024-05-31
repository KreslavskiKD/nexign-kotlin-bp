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
            -`get subscriber info`
            -`check subscriber promotions` binary {
                yes = route {
                    -`prolong promotion`
                    -`notify about promotion time period`
                    -end
                }
                no = route {
                    -`activate promotion`
                    -`write off money` multiple {
                        +(YES to route {
                            -`cancel promotion activation`
                            -`notify about error with promotion activation`
                            -end
                        })
                        +(NO to route {
                            -`notify about promotion activation`
                            -`notify about promotion time period`
                            -end
                        })
                    }
                }
            }
        } errorRouting {
            listOf(`activate promotion`, `cancel promotion activation`)  with ActionProblemsETC              togetherRoutesTo specialErrorHandling
            OperationDefault                                      with SomethingUnexpectedHappened    routesTo defaultErrorHandling
        }

        private val `get subscriber info` = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            // println("getting abonent action ${input.subscriber.id}")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val `prolong promotion` = Operation {
            // Do something
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val `activate promotion` = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            // println("activating action ${input.promotion.name}")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val `cancel promotion activation` = Operation {
            val input = it.input as ExampleScenarioInput

            // Do something
            // println("cancelling action ${input.promotion.name} activation")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val `check subscriber promotions` = Operation {
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

        private val `write off money` = Operation {
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

        private val `notify about promotion time period` = Operation {
            // println("Notifying about promotion time period")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val `notify about promotion activation` = Operation {
            // println("Notifying about promotion activation")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }

        private val `notify about error with promotion activation` = Operation {
            // println("Notifying about error with promotion activation")
            Thread.sleep(1000)
            SINGLE_ROUTE result None
        }
    }
}
