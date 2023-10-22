package com.bpavuk.filery

import com.bpavuk.filery.expects.BufferedFile
import com.bpavuk.filery.expects.Utils
import com.bpavuk.filery.expects.readUntil
import com.bpavuk.filery.types.FileType
import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
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
    path: String,
    private val createOnAbsence: Boolean = false
) {
    private val buffer = Buffer()
    private var bufferedFile = BufferedFile(Path(path), buffer, createOnAbsence)
    public val path: Path get() = bufferedFile.path

    public fun path(): String = path.path

    public fun open(
        path: Path = this.path,
        mod: Modes = Modes.ReadWrite,
        createOnAbsence: Boolean = this.createOnAbsence
    ): Filery {
        bufferedFile.close()
        bufferedFile = BufferedFile(path, buffer)
        if (!Utils.exists(path) && createOnAbsence) bufferedFile.createFile(path)
        return this
    }

    public fun go(
        path: Path = this.path,
        type: FileType = FileType.FILE,
        mod: Modes = Modes.ReadWrite,
        createOnAbsence: Boolean = this.createOnAbsence
    ) {
        bufferedFile = when (type) {
            FileType.FILE -> {
                bufferedFile.close()
                BufferedFile(path, buffer)
            }
            FileType.DIRECTORY -> bufferedFile.openDir(path)
        }
    }

    public fun go(
        path: String = this.path.path,
        type: FileType = FileType.FILE,
        mod: Modes = Modes.ReadWrite,
        createOnAbsence: Boolean = this.createOnAbsence
    ): Unit = go(Path(path), type, mod, createOnAbsence)

    public fun create(
        filename: Path,
        fileType: FileType = FileType.FILE
    ): Boolean =
        when (fileType) {
            FileType.FILE -> {
                bufferedFile.createFile(filename)
            }
            FileType.DIRECTORY -> {
                bufferedFile.createDir(filename)
            }
        }

    public fun delete(): Boolean = bufferedFile.delete()

    public fun close() {
        bufferedFile.close()
    }

    public suspend fun readBytes(amount: Int = -1): ByteArray {
        bufferedFile.readBytes(amount)
        return bufferedFile.readBuffer()
    }

    public suspend fun readUntil(includeLastByte: Boolean = true, condition: Byte.() -> Boolean): ByteArray {
        bufferedFile.readUntil(includeLastByte, condition)
        return bufferedFile.readBuffer()
    }

    public suspend fun readText(amount: Int = -1): String {
        bufferedFile.readBytes(amount)
        return bufferedFile.readBufferAsString()
    }

    public suspend fun readLine(cutLineEscape: Boolean = true): String = readUntil(!cutLineEscape) {
        this == '\n'.code.toByte()
    }.decodeToString()

    public suspend fun write(bytes: ByteArray) {
        bufferedFile.buffer.write(bytes)
        bufferedFile.writeToFile()
    }

    public suspend fun write(bytes: List<Byte>) {
        write(bytes.toByteArray())
    }

    public suspend fun write(text: String) {
        write(text.encodeToByteArray())
    }

    public suspend fun append(bytes: ByteArray) {
        bufferedFile.buffer.write(bytes)
        bufferedFile.appendToFile()
    }

    public suspend fun append(bytes: List<Byte>) {
        append(bytes.toByteArray())
    }

    public suspend fun append(text: String) {
        append(text.encodeToByteArray())
    }

    public fun fileExists(): Boolean = Utils.exists(path)
}

/**
 *  The Filery library starting point. It automatically opens your file by path, does what you wish
 *  to do and closes it safely. Preferred way to work with files
 */
public suspend inline fun filery(
    path: String,
    createFileOnAbsence: Boolean = false,
    mod: Modes = Modes.ReadWrite,
    noinline block: suspend (@FileryDsl Filery).() -> Unit
) {
    val filery = Filery(path, createFileOnAbsence).open(mod = mod)
    try {
        filery.block()
    } finally {
        filery.close()
    }
}
