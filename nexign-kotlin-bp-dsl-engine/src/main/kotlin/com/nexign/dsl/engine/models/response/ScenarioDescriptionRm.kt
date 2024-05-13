package com.nexign.dsl.engine.models.response

import com.nexign.dsl.base.description.DescriptionType
import com.nexign.dsl.base.description.ErrorRoutingShowState

data class ScenarioDescriptionRm(
    val scenarioClassName: String,
    val descriptionType: DescriptionType,
    val addErrorRouting: ErrorRoutingShowState,
)
