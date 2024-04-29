package com.nexign.dsl.engine.models.response

data class ScenarioDescriptionRm(
    val scenarioClassName: String,
    val descriptionType: DescriptionType,
)


enum class DescriptionType {
    TEXT,
    DOT_FILE,
    PICTURE,
}