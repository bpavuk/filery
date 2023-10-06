package com.bpavuk.filery

import com.bpavuk.filery.expects.FileContainerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
internal annotation class FileryDsl

/**
 * The [Filery] class used for imperative file management style. It does not open and close the files automatically,
 * so this routine is up to you. Or, use the declarative [filery] DSL, which is what this library is basically designed
 * around
 */
@Suppress("MemberVisibilityCanBePrivate")
public class Filery(public val path: String) {
    private val buffer = Buffer()
    private val container = FileContainerImpl(Path(path), buffer)

    public suspend fun open(mod: Modes = Modes.ReadWrite): Filery {
        if (!container.isOpen(mod)) container.open(mod)
        return this
    }

    public suspend fun isOpen(): Boolean = container.isOpen()

    public suspend fun close() {
        container.close()
    }

    public suspend fun readBytes(amount: Int = -1): ByteArray {

        TODO("""
            must read the file
        """.trimIndent())
    }

    public suspend fun readUntil(condition: Byte.() -> Boolean): ByteArray {
//        val byteArray: MutableList<Byte> = mutableListOf()
//        do {
//            val byte = readBytes(amount = 1)[0]
//            byteArray.add(byte)
//        } while (!byte.condition())
//        return ByteArray(byteArray.size) { byteArray[it] }

        TODO("""
            must be implemented on the native side
        """.trimIndent())
    }

    public suspend fun readText(amount: Int = -1): String {
        return readBytes(amount).decodeToString()
    }

    public suspend fun readLine(): ByteArray = readUntil { this == '\n'.code.toByte() }
}

/**
 *  The Filery library starting point. It automatically opens your file by path, does what you wish
 *  to do and closes it safely. Preferred way to work with files
 */
public suspend inline fun filery(path: String, noinline block: suspend (@FileryDsl Filery).() -> Unit) {
    coroutineScope {
        launch(Dispatchers.IO) {
            val filery = Filery(path).open()
            val potentialException = runCatching { filery.block() }.exceptionOrNull()
            filery.close()
            if (potentialException != null) {
                throw potentialException
            }
        }
    }
}

private fun main() = runBlocking {
    filery("fuckery.txt") {
        println(readLine())
    }
}
