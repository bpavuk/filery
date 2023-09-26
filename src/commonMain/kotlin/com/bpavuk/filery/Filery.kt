package com.bpavuk.filery

import kotlinx.coroutines.runBlocking

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
internal annotation class FileryDsl

public class Filery(val path: String) {
    public suspend fun open(): Filery {
        TODO("""
            must open the file, underlying implementations should store open file somewhere
            to make it possible to close it
        """.trimIndent())
    }

    public suspend fun close() {
        TODO("""
            must close the file
        """.trimIndent())
    }

    public suspend fun readBytes(amount: Int = -1): ByteArray {
        TODO("""
            must read the file
        """.trimIndent())
    }

    public suspend fun readUntil(condition: Byte.() -> Boolean): ByteArray {
        val byteArray: MutableList<Byte> = mutableListOf()
        do {
            val byte = readBytes(amount = 1)[0]
            byteArray.add(byte)
        } while (!byte.condition())
        return ByteArray(byteArray.size) { byteArray[it] }
    }

    public suspend fun readText(amount: Int = -1): String {
        return readBytes(amount).decodeToString()
    }

    public suspend fun readLine(): ByteArray = readUntil { this == '\n'.code.toByte() }
}

/**
 *  The Filery library starting point. It automatically opens your file by path, does what you wish
 *  to do and closes it safely
 */
public suspend inline fun filery(path: String, block: (@FileryDsl Filery).() -> Unit) {
    val filery = Filery(path).open()
    filery.block()
    filery.close()
}

fun main() = runBlocking {
    filery("fuckery.txt") {
        println(readLine())
    }
}