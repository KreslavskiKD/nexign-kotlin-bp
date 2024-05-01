package com.nexign.dsl.base.description

import com.nexign.dsl.base.OperationDefault
import com.nexign.dsl.base.transitions.*

data class ScenarioDescription (
    val scenarioName: String,
    var startingOperation: OperationDescription,
    val detailedDescription: String = "",
) {
    fun toText(showErrorRouting: ErrorRoutingShowState): String {
        val sb = StringBuilder()
        sb.append("Scenario name: $scenarioName\nDetailed description: $detailedDescription\n\n")

        val visited : MutableList<OperationDescription> = mutableListOf()

        var current = listOf(startingOperation)
        var next = listOf<OperationDescription>()

        while (current.isNotEmpty()) {
            for (operationDescription in current) {
                visited.add(operationDescription)
                sb.append(operationDescription.toText())
                for (tr in operationDescription.transitions) {
                    if (tr.key is ErrorTransitionCondition) {
                        when (showErrorRouting) {
                            ErrorRoutingShowState.NO -> continue
                            ErrorRoutingShowState.YES -> {}
                            ErrorRoutingShowState.YES_WITHOUT_DEFAULT -> {
                                if (tr.value.operationName == OperationDefault.javaClass.simpleName) {
                                    continue
                                }
                            }
                        }
                    }
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

    fun toDot(showErrorRouting: ErrorRoutingShowState): String {
        val sb = StringBuilder()
        sb.append("digraph $scenarioName {\n")

        val visited : MutableList<OperationDescription> = mutableListOf()

        var current = listOf(startingOperation)
        var next = listOf<OperationDescription>()

        while (current.isNotEmpty()) {
            for (operationDescription in current) {
                visited.add(operationDescription)
                val currentOpName = operationDescription.operationName
                for (tr in operationDescription.transitions) {
                    if (tr.key is ErrorTransitionCondition) {
                        when (showErrorRouting) {
                            ErrorRoutingShowState.NO -> continue
                            ErrorRoutingShowState.YES -> {}
                            ErrorRoutingShowState.YES_WITHOUT_DEFAULT -> {
                                if (tr.value.operationName == OperationDefault.javaClass.simpleName) {
                                    continue
                                }
                            }
                        }
                    }
                    if (!visited.contains(tr.value)) {
                        next = next.plus(tr.value)
                        var label = ""
                        if (tr.key == YES) {
                            label = "[label=\"Yes\"]"
                        } else if (tr.key == NO) {
                            label = "[label=\"No\"]"
                        }
                        sb.append("\t$currentOpName -> ${tr.value.operationName}$label;\n")
                    }
                }
            }
            current = next
            next = listOf()
        }

        sb.append("\n")

        for (operationDescription in visited) {
            if (operationDescription.operationName == "start") {
                continue
            }
            sb.append("\t${operationDescription.operationName} [style=rounded shape=rect]\n")
        }

        sb.append("\n\tstart [shape=doublecircle style=filled fillcolor=black fixedsize=true fontcolor=white]\n}\n")

        return sb.toString()
    }



    fun toPicture(showErrorRouting: ErrorRoutingShowState): String {
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

