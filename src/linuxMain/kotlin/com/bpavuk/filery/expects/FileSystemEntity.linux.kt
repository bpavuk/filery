package com.bpavuk.filery.expects

import com.bpavuk.filery.exceptions.FileDoesNotExistException
import com.bpavuk.filery.expects.pointers.DirectoryPointer
import com.bpavuk.filery.expects.pointers.FilePointer
import com.bpavuk.filery.types.Path
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.*


@OptIn(ExperimentalForeignApi::class)
public actual fun FileSystemEntity.createFile(path: Path): Boolean {
    return if (access(path.path, F_OK) == 0) {
        false
    } else {
        val pointer = fopen(path.path, "w")
        fclose(pointer)
        true
    }
}

public actual fun FileSystemEntity.createDir(path: Path): Boolean {
    return mkdir(path.path, (S_IRWXU or S_IRWXG or S_IRWXO).toUInt()) == 0
}

private fun S_ISREG(m: __mode_t): Boolean = m.toInt() and S_IFMT == S_IFREG

private fun S_ISDIR(m: __mode_t): Boolean = m.toInt() and S_IFMT == S_IFDIR

@OptIn(ExperimentalForeignApi::class)
public actual fun fileSystemEntityBuilder(
    path: Path,
    createFileOnNonExistence: Boolean
): FileSystemEntity {
    val statPtr = memScoped {
        alloc<stat>()
    }
    stat(path.path, statPtr.ptr)
    return if (S_ISREG(statPtr.st_mode)) {
        FileSystemEntity.File(
            path,
            FilePointer(path)
        )
    } else if (S_ISDIR(statPtr.st_mode)) {
        FileSystemEntity.Directory(
            path,
            DirectoryPointer(path)
        )
    } else if (createFileOnNonExistence) {
        val file = fopen(path.path, "w")
        fclose(file)
        fileSystemEntityBuilder(path, createFileOnNonExistence)
    } else {
        throw FileDoesNotExistException(path.path)
    }
}