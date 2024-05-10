package com.nexign.dsl.engine.rest.routers

import com.iyanuadelekan.kanary.core.KanaryRouter
import com.nexign.dsl.engine.rest.controllers.ScenariosController

class ScenariosRouter {

    val router = KanaryRouter()
    private val controller = ScenariosController()

    init {
        router on SCENARIOS_PATH use controller
        router.post(
            path = START_SCENARIO_PATH,
            controller::startScenario,
        )
        router.get(
            path = GET_SCENARIO_DESCRIPTION_PATH,
            controller::getScenarioDescription,
        )
    }

    companion object {
        private const val SCENARIOS_PATH = "/scenarios"
        private const val START_SCENARIO_PATH = "/start"
        private const val GET_SCENARIO_DESCRIPTION_PATH = "/description"
    }
}
