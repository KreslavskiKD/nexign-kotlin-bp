package com.nexign.dsl.base

// It is insignificant, but why not
inline fun classOpFromStackTraces(): String = Thread.currentThread().stackTrace[1].className
