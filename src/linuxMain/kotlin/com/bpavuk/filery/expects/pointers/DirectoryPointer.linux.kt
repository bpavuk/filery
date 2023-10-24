package com.bpavuk.filery.expects.pointers

import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path

public actual class DirectoryPointer actual constructor(
    override val path: Path,
    override val mode: Modes
) : IDirectoryPointer {
    override val platformDirectoryPointer: Any
        get() = TODO("Not yet implemented")

    override suspend fun listFiles(): List<Path> {
        TODO("Not yet implemented")
    }

    override fun go(relativePath: String, mode: Modes): IDirectoryPointer {
        TODO("Not yet implemented")
    }

    override fun create(relativePath: String): IDirectoryPointer {
        TODO("Not yet implemented")
    }

    override fun delete(): Boolean {
        TODO("Not yet implemented")
    }
}