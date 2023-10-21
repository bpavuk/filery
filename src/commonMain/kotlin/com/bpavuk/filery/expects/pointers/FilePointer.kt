package com.bpavuk.filery.expects.pointers

import com.bpavuk.filery.types.FileType
import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path

public interface IFilePointer {
    public val path: Path
    public val platformFilePointer: Any
    public val type: FileType
    public val mode: Modes

    public fun close(): Boolean

    public suspend fun readBytes(amount: Int? = null): ByteArray

    public suspend fun writeBytes(bytes: ByteArray)

    public suspend fun appendBytes(bytes: ByteArray)

    public suspend fun delete(): Boolean
}

public expect class FilePointer : IFilePointer

