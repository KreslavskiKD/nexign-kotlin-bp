package com.nexign.dsl.base.exceptions

class IllegalScenarioArgumentException(override val message: String?) : IllegalArgumentException(message)

class NoSuchOperationException() : NoSuchElementException()
