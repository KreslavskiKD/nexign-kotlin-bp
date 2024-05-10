package com.nexign.dsl.engine.rest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.iyanuadelekan.kanary.core.KanaryController
import com.iyanuadelekan.kanary.helpers.http.request.done
import com.iyanuadelekan.kanary.helpers.http.response.sendJson
import com.iyanuadelekan.kanary.helpers.http.response.withStatus
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ScenariosController : KanaryController() {

    fun startScenario(baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        // TODO: change code
        val mapper = ObjectMapper()
        val responseRootNode = mapper.createObjectNode()
        with(responseRootNode) {
            put("hello", "world")
        }
        response withStatus 201 sendJson responseRootNode
        baseRequest.done()
    }

    fun getScenarioDescription(baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        // TODO: change code
        val mapper = ObjectMapper()
        val responseRootNode = mapper.createObjectNode()
        with(responseRootNode) {
            put("hello", "world")
        }
        response withStatus 201 sendJson responseRootNode
        baseRequest.done()
    }
}