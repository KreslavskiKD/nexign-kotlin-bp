package com.nexign.dsl.engine.models.response

import com.nexign.dsl.base.description.DescriptionType
import com.nexign.dsl.base.description.ErrorRoutingShowState

data class ScenarioDescriptionRm(
    val scenarioClassName: String,
    val inputClassName: String,     // this is maybe not he best solution yet, but for now I don't know how to make it better
    val dummyInput: String,
    val descriptionType: DescriptionType,
    val addErrorRouting: ErrorRoutingShowState,
)
