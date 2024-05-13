package com.nexign.dsl.engine.rest.routers

import com.nexign.dsl.engine.Application
import com.nexign.dsl.engine.models.response.ScenarioDescriptionRm
import com.nexign.dsl.engine.models.response.ScenarioStartRm
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.route
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import java.nio.file.Paths
import kotlin.io.path.Path

class ScenariosRouter(
    private val startScenarioAdapter: JsonAdapter<ScenarioStartRm>,
    private val descriptionAdapter: JsonAdapter<ScenarioDescriptionRm>,
    private val dslApplication: Application,
) {

    fun routeScenarios(ktorApp: io.ktor.server.application.Application) {
        ktorApp.routing {
            swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
                version = "4.15.5"
            }
            openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml") {
                codegen = StaticHtmlCodegen()
            }

            route(SCENARIOS_PATH) {
                postStartScenarioRoute()
                postGetScenarioDescription()
                getImage()
            }
        }
    }

    private fun Route.postStartScenarioRoute() {
        route(START_SCENARIO_PATH) {
            post {
                val jsonString = call.receiveText()         // TODO : https://ktor.io/docs/server-responses.html#object
                try {
                    val startScenarioJson = startScenarioAdapter.fromJson(jsonString)
                        ?: throw JsonDataException("scenario request failed to parse or is null")
                    val result = dslApplication.startScenario(startScenarioJson)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result,
                    )
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = e.message ?: "",  // TODO: think of error messages
                    )
                }
            }
        }
    }

    private fun Route.postGetScenarioDescription() {
        route(GET_SCENARIO_DESCRIPTION_PATH) {
            post {
                val jsonString = call.receiveText()
                try {
                    val getDescriptionJson = descriptionAdapter.fromJson(jsonString)
                        ?: throw JsonDataException("scenario description request failed to parse or is null")
                    val result = dslApplication.getScenarioDescription(getDescriptionJson)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result,
                    )
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = e.message ?: "",  // TODO: think of error messages
                    )
                }
            }
        }
    }

    private fun Route.getImage() {
        route(GET_IMAGE) {
            get("/{path}") {
                try {
                    if ((call.parameters["path"] != null) && (call.parameters["path"] != "")) {
                        val cwd = Paths.get("").toAbsolutePath().toString()
                        val pathString = "$cwd/scenarios/image/${call.parameters["path"]}"
                        val path = Path(pathString).toAbsolutePath()
                        val file = path.toFile()
                        call.response.header(
                            HttpHeaders.ContentType,
                            ContentType.Image.PNG.contentType,
                        )
                        call.respondFile(file)
                    } else {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = "",           // TODO: think of error messages
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = e.message ?: "",  // TODO: think of error messages
                    )
                }
            }
        }
    }

    companion object {
        private const val SCENARIOS_PATH = "/scenarios"
        private const val START_SCENARIO_PATH = "/start"
        private const val GET_SCENARIO_DESCRIPTION_PATH = "/description"
        private const val GET_IMAGE = "/image"
    }
}
