package com.nexign.dsl.engine.storage

import com.nexign.dsl.base.exceptions.IllegalScenarioArgumentException
import com.nexign.dsl.engine.logging.Logger

class Storage(
    private val params: MutableMap<String, Any>,
    private val logger: Logger,
) : MutableMap<String, Any> by params {

    override operator fun get(key: String): Any? {
        logger.log("wanted to get value named $key")
        return if (params.containsKey(key)) {
            params[key]
        } else {
            throw IllegalScenarioArgumentException("No value with key $key exists")
        }
    }

    operator fun set(key: String, value: Any) {
        logger.log("put value $value named $key")
        params[key] = value
    }
}