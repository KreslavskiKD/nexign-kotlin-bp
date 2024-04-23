package com.nexign.dsl.engine.exceptions

open class NexignBpException(
    override val message: String?,
    override val cause: Throwable? = null,
) : Exception(message, cause) {
}

class NexignBpIllegalClassProvidedException(
    override val message: String?,
    override val cause: Throwable? = null,
) : NexignBpException(message, cause) {
}