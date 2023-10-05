package com.bpavuk.testing

import kotlinx.coroutines.runBlocking

inline fun <reified T : Throwable> assertThrowing(crossinline block: suspend () -> Unit) {
    var throwable: T? = null

    try {
        runBlocking { block() }
    } catch (t: Throwable) {
        if (t is T) {
            throwable = t
        } else {
            throw t
        }
    }

    if (throwable == null) {
        error("${T::class.simpleName} was expected to be thrown, but nothing was")
    }
}
