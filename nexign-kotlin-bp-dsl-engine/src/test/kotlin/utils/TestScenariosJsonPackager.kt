package utils

import com.nexign.dsl.engine.models.response.DescriptionType
import com.nexign.dsl.engine.models.response.ScenarioDescriptionRm
import com.nexign.dsl.engine.models.response.ScenarioStartRm
import scenarios.arithmeticscenario.ArithmeticInput
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin.test.Test


internal class TestScenariosJsonPackager {
    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    @OptIn(ExperimentalStdlibApi::class)
    val startScenarioJsonAdapter: JsonAdapter<ScenarioStartRm> = moshi.adapter<ScenarioStartRm>()

    @OptIn(ExperimentalStdlibApi::class)
    val inputJsonAdapter: JsonAdapter<ArithmeticInput> = moshi.adapter<ArithmeticInput>()

    @OptIn(ExperimentalStdlibApi::class)
    val scenarioDescriptionJsonAdapter: JsonAdapter<ScenarioDescriptionRm> = moshi.adapter<ScenarioDescriptionRm>()

    @Test
    fun printStartArithmeticScenarioJson() {
        val request = ScenarioStartRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            inputClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput",
            input = inputJsonAdapter.toJson(ArithmeticInput(
                a = 12.0,
                b = 5.5,
            )),
        )

        val requestJson = startScenarioJsonAdapter.toJson(request)
        println(requestJson)
    }

    @Test
    fun printArithmeticScenarioTextDescriptionJson() {
        val request = ScenarioDescriptionRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            inputClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput",
            dummyInput = inputJsonAdapter.toJson(ArithmeticInput(
                a = 12.0,
                b = 5.5,
            )),
            descriptionType = DescriptionType.TEXT,
        )

        val requestJson = scenarioDescriptionJsonAdapter.toJson(request)
        println(requestJson)
    }

    @Test
    fun printArithmeticScenarioDotDescriptionJson() {
        val request = ScenarioDescriptionRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            inputClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput",
            dummyInput = inputJsonAdapter.toJson(ArithmeticInput(
                a = 12.0,
                b = 5.5,
            )),
            descriptionType = DescriptionType.DOT_FILE,
        )

        val requestJson = scenarioDescriptionJsonAdapter.toJson(request)
        println(requestJson)
    }

    @Test
    fun printArithmeticScenarioPictureDescriptionJson() {
        val request = ScenarioDescriptionRm(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            inputClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput",
            dummyInput = inputJsonAdapter.toJson(ArithmeticInput(
                a = 12.0,
                b = 5.5,
            )),
            descriptionType = DescriptionType.PICTURE,
        )

        val requestJson = scenarioDescriptionJsonAdapter.toJson(request)
        println(requestJson)
    }
}