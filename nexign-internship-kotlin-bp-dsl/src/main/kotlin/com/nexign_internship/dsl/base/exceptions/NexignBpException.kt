package com.nexign.internship.dsl.base.exceptions

open class NexignBpException(
    override val message: String?,
    override val cause: Throwable? = null,
) : Exception(message, cause)