package com.nexign.dsl.base.description

import com.nexign.dsl.base.transitions.NamedTC
import com.nexign.dsl.base.transitions.NumberedTC
import com.nexign.dsl.base.transitions.TransitionCondition

data class ScenarioDescription (
    val scenarioName: String,
    var startingOperation: OperationDescription,
    val detailedDescription: String = "",
) {
    fun toText(): String {
        val sb = StringBuilder()
        sb.append("Scenario name: $scenarioName\nDetailed description: $detailedDescription\n\n")

        val visited : MutableList<OperationDescription> = mutableListOf()

        var current = listOf(startingOperation)
        var next = listOf<OperationDescription>()

        while (current.isNotEmpty()) {
            for (od in current) {
                visited.add(od)
                sb.append(od.toText())
                for (tr in od.transitions) {
                    if (!visited.contains(tr.value)) {
                        next = next.plus(tr.value)
                    }
                }
            }
            current = next
            next = listOf()
        }

        return sb.toString()
    }

    fun toDot(): String {
        TODO()
    }

    fun toPicture(): String {
        TODO()
    }
}

data class OperationDescription (
    val operationName: String,
    var transitions: MutableMap<TransitionCondition, OperationDescription>,
    val detailedDescription: String = "",
) {
    fun toText(): String {
        val sb = StringBuilder()
        sb.append("Operation name: $operationName\n")
        sb.append("Detailed description: $detailedDescription\n")

        for (tr in transitions) {
            when (tr.key) {
                is NumberedTC -> {
                    sb.append("In case of transition condition ${tr.key.javaClass.simpleName} with number ${(tr.key as NumberedTC).number} go to ${tr.value.operationName}\n" )
                }
                is NamedTC -> {
                    sb.append("In case of transition condition ${tr.key.javaClass.simpleName} with name ${(tr.key as NamedTC).name} go to ${tr.value.operationName}\n" )
                }
                else -> {
                    sb.append("In case of transition condition ${tr.key.javaClass.simpleName} go to ${tr.value.operationName}\n" )
                }
            }
        }

        return sb.toString()
    }
}

