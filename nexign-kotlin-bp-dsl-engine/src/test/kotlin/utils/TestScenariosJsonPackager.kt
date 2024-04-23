package utils

import com.nexign.dsl.engine.transport.ScenarioRequest
import scenarios.arithmeticscenario.ArithmeticInput
import scenarios.arithmeticscenario.ArithmeticScenario
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin.test.Test


internal class TestScenariosJsonPackager {
    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    @OptIn(ExperimentalStdlibApi::class)
    val jsonAdapter: JsonAdapter<ScenarioRequest> = moshi.adapter<ScenarioRequest>()

    @OptIn(ExperimentalStdlibApi::class)
    val inputJsonAdapter: JsonAdapter<ArithmeticInput> = moshi.adapter<ArithmeticInput>()

    @Test
    fun printArithmeticScenarioJson() {
        val request = ScenarioRequest(
            scenarioClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticScenario",
            inputClassName = "com.nexign.dsl.scenarios.examples.arithmeticscenario.ArithmeticInput",
            input = inputJsonAdapter.toJson(ArithmeticInput(
                a = 12.0,
                b = 5.5,
            )),
        )

        val requestJson = jsonAdapter.toJson(request)
        println(requestJson)
    }
}