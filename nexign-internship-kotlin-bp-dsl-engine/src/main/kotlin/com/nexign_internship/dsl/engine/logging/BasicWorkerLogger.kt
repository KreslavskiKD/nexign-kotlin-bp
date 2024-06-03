package com.nexign_internship.dsl.engine.logging

// Very primitive for now
class BasicWorkerLogger: Logger {
    private val currentRun: MutableList<RunStage> = mutableListOf()

    override fun log(info: String) {
        currentRun.add(RunStage(info))
    }

    override fun getLog(): List<RunStage> {
        return currentRun
    }

    override fun clear() {
        currentRun.clear()
    }
}
