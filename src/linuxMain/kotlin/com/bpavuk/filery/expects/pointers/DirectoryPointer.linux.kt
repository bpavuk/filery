package com.bpavuk.filery.expects.pointers

import com.bpavuk.filery.exceptions.FileDoesNotExistException
import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.*

public actual class DirectoryPointer actual constructor(
    override val path: Path,
    override val mode: Modes
) : IDirectoryPointer {
    @OptIn(ExperimentalForeignApi::class)
    override var platformDirectoryPointer: CPointer<DIR> = opendir(path.path) ?: throw FileDoesNotExistException(path.path)
        private set

    override suspend fun listFiles(): List<Path> {
        TODO("Not yet implemented")
    }

    override fun go(relativePath: String, mode: Modes): IDirectoryPointer {
        val fullPath = if (relativePath.startsWith('/')) Path(relativePath) else path + Path(relativePath)
        return DirectoryPointer(fullPath, mode)
    }

    override fun create(relativePath: String, mode: Modes): IDirectoryPointer {
        val fullPath = if (relativePath.startsWith('/')) Path(relativePath) else path + Path(relativePath)
        mkdir(fullPath.path, (S_IRWXU or S_IRWXG or S_IRWXO).toUInt())
        return go(fullPath.path, mode)
    }

    override fun delete(): Boolean {
        close()
        return rmdir(path.path) == 0
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun close(): Boolean {
        return closedir(platformDirectoryPointer) == 0
    }
}