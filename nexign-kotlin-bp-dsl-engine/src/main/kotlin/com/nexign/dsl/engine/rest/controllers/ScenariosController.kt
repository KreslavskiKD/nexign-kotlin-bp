package com.nexign.dsl.engine.rest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.iyanuadelekan.kanary.core.KanaryController
import com.iyanuadelekan.kanary.helpers.http.request.done
import com.iyanuadelekan.kanary.helpers.http.request.getBodyAsJson
import com.iyanuadelekan.kanary.helpers.http.response.sendJson
import com.iyanuadelekan.kanary.helpers.http.response.withStatus
import com.nexign.dsl.engine.Application
import com.nexign.dsl.engine.models.response.ScenarioDescriptionRm
import com.nexign.dsl.engine.models.response.ScenarioStartRm
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ScenariosController(
    private val startScenarioAdapter: JsonAdapter<ScenarioStartRm>,
    private val descriptionAdapter: JsonAdapter<ScenarioDescriptionRm>,
    private val application: Application,
) : KanaryController() {

    fun startScenario(baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        val mapper = ObjectMapper()
        val responseRootNode = mapper.createObjectNode()

        val jsonBody = request.getBodyAsJson()

        try {
            val startScenarioRequest: ScenarioStartRm = startScenarioAdapter.fromJson(jsonBody)
                ?: throw JsonDataException("scenario request failed to parse or is null")

            val appResponse = application.startScenario(startScenarioRequest)

            with(responseRootNode) {
                put("result", appResponse)
            }
            response withStatus 200 sendJson responseRootNode

        } catch (e: Exception) {
            response withStatus 400 sendJson responseRootNode
        }

        baseRequest.done()
    }

    fun getScenarioDescription(baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        val mapper = ObjectMapper()
        val responseRootNode = mapper.createObjectNode()

        val jsonBody = request.getBodyAsJson()

        try {
            val scenarioDescriptionRequest: ScenarioDescriptionRm = descriptionAdapter.fromJson(jsonBody)
                ?: throw JsonDataException("scenario description request failed to parse or is null")

            val appResponse = application.getScenarioDescription(scenarioDescriptionRequest)

            with(responseRootNode) {
                put("result", appResponse)
            }

            response withStatus 200 sendJson responseRootNode

        } catch (e: Exception) {
            response withStatus 400 sendJson responseRootNode
        }

        baseRequest.done()
    }
}