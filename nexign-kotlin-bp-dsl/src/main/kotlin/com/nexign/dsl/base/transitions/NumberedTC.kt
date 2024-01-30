package com.nexign.dsl.base.transitions

open class NumberedTC(val number: Int) : TransitionCondition()

object NumberedTCMap {
    private val numberedTCs = mutableMapOf<Int, NumberedTC>()

    fun getNumberedTC(num : Int) : TransitionCondition {
        return if (numberedTCs.containsKey(num)) {
            numberedTCs[num]!!
        } else {
            val ntc = NumberedTC(num)
            numberedTCs[num] = ntc
            ntc
        }
    }
}
