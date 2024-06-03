package com.nexign_internship.dsl.engine.models.response

import com.nexign.internship.dsl.base.description.DescriptionType
import com.nexign.internship.dsl.base.description.ErrorRoutingShowState

data class ScenarioDescriptionRm(
    val scenarioClassName: String,
    val descriptionType: DescriptionType,
    val addErrorRouting: ErrorRoutingShowState,
)
