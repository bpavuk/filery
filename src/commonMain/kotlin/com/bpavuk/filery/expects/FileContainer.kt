package com.bpavuk.filery.expects

import com.bpavuk.filery.Modes
import com.bpavuk.filery.Path
import kotlinx.io.Buffer

public interface FileContainer {
    public var file: FileryFile?
    public val buffer: Buffer
    public var mode: Modes?

    public suspend fun isOpen(mode: Modes = Modes.ReadWrite): Boolean =
        file != null && this.mode == mode

    public suspend fun open(mode: Modes)

    public suspend fun close() {
        file = null
        mode = null
    }
}

internal expect class FileContainerImpl(
    path: Path,
    buffer: Buffer
) : FileContainer

internal typealias FileryFile = Any
