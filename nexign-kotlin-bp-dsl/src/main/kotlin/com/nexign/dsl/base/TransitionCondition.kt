package com.nexign.dsl.base

open class TransitionCondition

object SINGLE_ROUTE : TransitionCondition()

object STOP_EXECUTION : TransitionCondition()
object START_EXECUTION : TransitionCondition()

open class BinaryTC : TransitionCondition()

object YES : BinaryTC()
object NO : BinaryTC()

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