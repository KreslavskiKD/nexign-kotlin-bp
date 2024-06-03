package utils.manual_testing

import com.nexign.internship.dsl.base.description.DescriptionType
import com.nexign.internship.dsl.base.description.ErrorRoutingShowState
import com.nexign.internship.dsl.base.scenario.data.Input
import com.nexign_internship.dsl.engine.models.response.ScenarioDescriptionRm
import com.nexign_internship.dsl.engine.models.response.ScenarioStartRm
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin.test.Test


data class ArithmeticInput(
    val a: Double,
    val b: Double,
) : Input

data class Subscriber(
    var id: String
)

data class Promotion(
    var name: String
)

data class ExampleScenarioInput(
    val subscriber: Subscriber,
    val promotion: Promotion,
) : Input

internal class TestScenariosJsonPackager {
    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    @OptIn(ExperimentalStdlibApi::class)
    val startScenarioJsonAdapter: JsonAdapter<ScenarioStartRm> = moshi.adapter<ScenarioStartRm>()

    @OptIn(ExperimentalStdlibApi::class)
    val arithmeticInputJsonAdapter: JsonAdapter<ArithmeticInput> = moshi.adapter<ArithmeticInput>()

    @OptIn(ExperimentalStdlibApi::class)
    val bpInputJsonAdapter: JsonAdapter<ExampleScenarioInput> = moshi.adapter<ExampleScenarioInput>()

    @OptIn(ExperimentalStdlibApi::class)
    val scenarioDescriptionJsonAdapter: JsonAdapter<ScenarioDescriptionRm> = moshi.adapter<ScenarioDescriptionRm>()

    @Test
    fun printStartArithmeticScenarioJson() {
        val request = ScenarioStartRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            inputClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput",
            input = arithmeticInputJsonAdapter.toJson(ArithmeticInput(
                a = 12.0,
                b = 5.5,
            )),
        )

        val requestJson = startScenarioJsonAdapter.toJson(request)
        println(requestJson)
    }

    @Test
    fun printStartExampleScenarioJson() {
        val request = ScenarioStartRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.bpscenario.ExampleScenario",
            inputClassName = "com.nexign.dsl.scenarios.examples.bpscenario.ExampleScenarioInput",
            input = bpInputJsonAdapter.toJson(ExampleScenarioInput(
                subscriber = Subscriber("some_id"),
                promotion = Promotion("promotion_name")
            )),
        )

        val requestJson = startScenarioJsonAdapter.toJson(request)
        println(requestJson)
    }

    @Test
    fun printArithmeticScenarioTextDescriptionJson() {
        val request = ScenarioDescriptionRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            addErrorRouting = ErrorRoutingShowState.NO,
            descriptionType = DescriptionType.TEXT,
        )

        val requestJson = scenarioDescriptionJsonAdapter.toJson(request)
        println(requestJson)
    }

    @Test
    fun printArithmeticScenarioDotDescriptionJson() {
        val request = ScenarioDescriptionRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            addErrorRouting = ErrorRoutingShowState.NO,
            descriptionType = DescriptionType.DOT_FILE,
        )

        val requestJson = scenarioDescriptionJsonAdapter.toJson(request)
        println(requestJson)
    }

    @Test
    fun printArithmeticScenarioPictureDescriptionJson() {
        val request = ScenarioDescriptionRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            addErrorRouting = ErrorRoutingShowState.NO,
            descriptionType = DescriptionType.PICTURE,
        )

        val requestJson = scenarioDescriptionJsonAdapter.toJson(request)
        println(requestJson)
    }

    @Test
    fun printExampleScenarioPictureDescriptionJson() {
        val request = ScenarioDescriptionRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.bpscenario.ExampleScenario",
            addErrorRouting = ErrorRoutingShowState.NO,
            descriptionType = DescriptionType.PICTURE,
        )

        val requestJson = scenarioDescriptionJsonAdapter.toJson(request)
        println(requestJson)
    }
}