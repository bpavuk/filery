package com.bpavuk.filery

import com.bpavuk.filery.expects.FileContainerImpl
import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
public class Filery(
    public val path: String,
    private val createOnAbsence: Boolean = false
) {
    private val buffer = Buffer()
    private val fileContainer = FileContainerImpl(Path(path), buffer)

    public fun path(): String = path

    public suspend fun open(mod: Modes = Modes.ReadWrite): Filery {
        if (createOnAbsence && !fileContainer.exists()) fileContainer.create()
        if (!fileContainer.isOpen(mod)) fileContainer.open(mod)
        return this
    }

    public suspend fun isOpen(): Boolean = fileContainer.isOpen()

    public suspend fun close() {
        fileContainer.close()
    }

    public suspend fun readBytes(amount: Int = -1): ByteArray {
        fileContainer.readBytes(amount)
        return fileContainer.readBuffer()
    }

    public suspend fun readUntil(includeLastByte: Boolean = true, condition: Byte.() -> Boolean): ByteArray {
        fileContainer.readUntil(includeLastByte, condition)
        return fileContainer.readBuffer()
    }

    public suspend fun readText(amount: Int = -1): String {
        fileContainer.readBytes(amount)
        return fileContainer.readBufferAsString()
    }

    public suspend fun readLine(cutLineEscape: Boolean = true): String = readUntil(!cutLineEscape) {
        this == '\n'.code.toByte()
    }.decodeToString()

    public suspend fun write(bytes: ByteArray) {
        fileContainer.writeBytes(bytes)
        fileContainer.writeToFile()
    }

    public suspend fun write(bytes: List<Byte>) {
        write(bytes.toByteArray())
    }

    public suspend fun write(text: String) {
        write(text.encodeToByteArray())
    }

    public suspend fun append(bytes: ByteArray) {
        fileContainer.writeBytes(bytes)
        fileContainer.appendToFile()
    }

    public suspend fun fileExists(): Boolean = fileContainer.exists()
}

/**
 *  The Filery library starting point. It automatically opens your file by path, does what you wish
 *  to do and closes it safely. Preferred way to work with files
 */
public suspend inline fun filery(
    path: String,
    createFileOnAbsence: Boolean = false,
    noinline block: suspend (@FileryDsl Filery).() -> Unit
) {
    coroutineScope {
        launch(Dispatchers.IO) {
            val filery = Filery(path, createFileOnAbsence).open()
            val potentialException = runCatching { filery.block() }.exceptionOrNull()
            filery.close()
            if (potentialException != null) {
                throw potentialException
            }
        }
    }
}
