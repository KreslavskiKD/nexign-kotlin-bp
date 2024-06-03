package com.nexign.internship.dsl.base.scenario.data

interface Input {}

interface Results {
    var error: String
}

class EmptyInput : Input

class DefaultResult : Results {
    override var error: String = ""
}
