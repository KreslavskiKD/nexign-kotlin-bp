package com.nexign.dsl.engine.rest.routers

import com.iyanuadelekan.kanary.core.KanaryRouter
import com.nexign.dsl.engine.Application
import com.nexign.dsl.engine.models.response.ScenarioDescriptionRm
import com.nexign.dsl.engine.models.response.ScenarioStartRm
import com.nexign.dsl.engine.rest.controllers.ScenariosController
import com.squareup.moshi.JsonAdapter

class ScenariosRouter(
    private val startScenarioAdapter: JsonAdapter<ScenarioStartRm>,
    private val descriptionAdapter: JsonAdapter<ScenarioDescriptionRm>,
    private val application: Application,
) {

    val router = KanaryRouter()
    private val controller = ScenariosController(
        startScenarioAdapter,
        descriptionAdapter,
        application,
    )

    init {
        router on SCENARIOS_PATH use controller
        router.post(
            path = START_SCENARIO_PATH,
            controller::startScenario,
        )
        router.post(
            path = GET_SCENARIO_DESCRIPTION_PATH,
            controller::getScenarioDescription,
        )
    }

    companion object {
        private const val SCENARIOS_PATH = "scenarios/"
        private const val START_SCENARIO_PATH = "start"
        private const val GET_SCENARIO_DESCRIPTION_PATH = "description"
    }
}
