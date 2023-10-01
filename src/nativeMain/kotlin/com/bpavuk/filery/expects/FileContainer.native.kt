package com.bpavuk.filery.expects

import com.bpavuk.filery.Path

internal actual class FileContainerImpl actual constructor(path: Path) : FileContainer {
    override val file: FileryFile
        get() = TODO("Not yet implemented")

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun open() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}