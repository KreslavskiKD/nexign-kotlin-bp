package com.nexign.internship.dsl.base.exceptions

class NexignBpIllegalScenarioArgumentException(
    override val message: String?,
    override val cause: Throwable? = null,
) : NexignBpException(message, cause)

class NexignBpNoSuchOperationException(
    override val message: String?,
    override val cause: Throwable? = null,
) : NexignBpException(message, cause)

class NexignBpIllegalClassProvidedException(
    override val message: String?,
    override val cause: Throwable? = null,
) : NexignBpException(message, cause)
