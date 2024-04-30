package com.nexign.dsl.engine.models.response

data class ScenarioDescriptionRm(
    val scenarioClassName: String,
    val inputClassName: String,     // this is maybe not he best solution yet, but for now I don't know how to make it better
    val dummyInput: String,
    val descriptionType: DescriptionType,
)

enum class DescriptionType {
    TEXT,
    DOT_FILE,
    PICTURE,
}