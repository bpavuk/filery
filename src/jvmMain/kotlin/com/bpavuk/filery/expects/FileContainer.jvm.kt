package com.bpavuk.filery.expects

import com.bpavuk.filery.Modes
import com.bpavuk.filery.Path
import kotlinx.io.Buffer
import java.io.File

internal actual class FileContainerImpl actual constructor(
    private val path: Path,
    override val buffer: Buffer
) : FileContainer {
    override var file: FileryFile? = null
    override var mode: Modes? = null

    override suspend fun open(mode: Modes) {
        val file = File(path.path)
        if (file.exists()) this.file = file
        this.mode = mode
    }

    override suspend fun close() {
        file = null
    }

    override suspend fun isDirectory(): Boolean = (file as File?)?.isFile == true
}
