package com.nexign.dsl.engine.transport

class ScenarioRequest(
    val scenarioClassName: String,
    val inputClassName: String,
    val inputConstructorParameters: List<Any>,
)
