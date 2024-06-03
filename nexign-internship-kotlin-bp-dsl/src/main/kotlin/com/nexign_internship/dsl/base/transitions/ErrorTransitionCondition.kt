package com.nexign.internship.dsl.base.transitions

open class ErrorTransitionCondition : TransitionCondition()

object ActionProblemsETC : ErrorTransitionCondition()

object SomethingUnexpectedHappened : ErrorTransitionCondition()
