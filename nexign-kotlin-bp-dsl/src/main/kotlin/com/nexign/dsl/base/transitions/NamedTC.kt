package com.nexign.dsl.base.transitions

open class NamedTC(val name: String) : TransitionCondition()

object NamedTCMap {
    private val namedTCs = mutableMapOf<String, NamedTC>()

    fun getNamedTC(name : String) : TransitionCondition {
        return if (namedTCs.containsKey(name)) {
            namedTCs[name]!!
        } else {
            val ntc = NamedTC(name)
            namedTCs[name] = ntc
            ntc
        }
    }
}
