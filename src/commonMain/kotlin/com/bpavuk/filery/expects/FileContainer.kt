package com.bpavuk.filery.expects

import com.bpavuk.filery.types.FileType
import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readString

public interface FileContainer {
    public var file: FileryFile?
    public var type: FileType?
    public val buffer: Buffer

    public var mode: Modes?

    public suspend fun isOpen(mode: Modes = Modes.ReadWrite): Boolean =
        file != null && this.mode == mode

    // TODO: implement modes
    public suspend fun open(mode: Modes)

    public suspend fun create()

    public suspend fun exists(): Boolean

    public suspend fun close() {
        file = null
        mode = null
    }

    /**
     * reads bytes to [buffer]
     */
    public suspend fun readBytes(amount: Int)

    /**
     * reads bytes to [buffer] until condition gives true
     */
    public suspend fun readUntil(includeLastByte: Boolean = true, condition: Byte.() -> Boolean)

    /**
     * writes bytes to [buffer]
     */
    public suspend fun writeBytes(bytes: ByteArray)

    /**
     * checks if current file is directory
     */
    public suspend fun isDirectory(): Boolean

    /**
     * checks if current file is regular file (needed to avoid symlinks and other shenanigans)
     */
    public suspend fun isRegularFile(): Boolean

    /**
     * reads the whole buffer and returns its contents
     */
    public suspend fun readBuffer(): ByteArray = buffer.readByteArray()

    /**
     * same as [readBuffer] but returns content as string
     */
    public suspend fun readBufferAsString(): String = buffer.readString()

    /**
     * writes directly from [buffer] to file, overwriting it
     */
    public suspend fun writeToFile()

    /**
     * appends bytes from [buffer] to file instead of overwriting it
     */
    public suspend fun appendToFile()

    /**
     * deletes the file and cleans the [buffer]
     * @return boolean that indicates whether deletion was successful
     */
    public suspend fun delete(): Boolean
}

internal expect class FileContainerImpl(
    path: Path,
    buffer: Buffer
) : FileContainer

internal typealias FileryFile = Any
