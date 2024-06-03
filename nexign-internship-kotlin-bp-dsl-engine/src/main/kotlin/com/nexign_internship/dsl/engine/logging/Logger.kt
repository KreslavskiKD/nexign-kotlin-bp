package com.nexign_internship.dsl.engine.logging


interface Logger {

    // It is yet very primitive, TODO: Improve
    fun log(info: String)

    fun getLog(): List<RunStage>

    fun clear()
}
