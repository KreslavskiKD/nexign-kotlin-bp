package com.nexign_internship.dsl.engine.models.response

data class ScenarioStartRm(
    val scenarioClassName: String,
    val inputClassName: String,
    val input: String,
)
