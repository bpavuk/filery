package com.bpavuk.filery.expects.pointers

import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
import java.io.File

public actual class DirectoryPointer actual constructor(
    override val path: Path,
    override val mode: Modes
) : IDirectoryPointer {

    // I know that this is overkill but nothing else can be applied here
    override val platformDirectoryPointer: File = File(path.path)

    override suspend fun listFiles(): List<Path> =
        platformDirectoryPointer.list()?.map { Path(it) } ?: emptyList()

    override fun go(relativePath: String, mode: Modes): IDirectoryPointer {
        TODO("Not yet implemented")
    }

    override fun create(relativePath: String, mode: Modes): IDirectoryPointer {
        TODO("Not yet implemented")
    }

    override fun delete(): Boolean = platformDirectoryPointer.delete()

    override fun close(): Boolean {
        TODO("Not yet implemented")
    }
}
