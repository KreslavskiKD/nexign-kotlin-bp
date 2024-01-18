package com.nexign.dsl.base.transitions

open class TransitionCondition

object SINGLE_ROUTE : TransitionCondition()

object STOP_EXECUTION : TransitionCondition()
object START_EXECUTION : TransitionCondition()

open class BinaryTC : TransitionCondition()

object YES : BinaryTC()
object NO : BinaryTC()

