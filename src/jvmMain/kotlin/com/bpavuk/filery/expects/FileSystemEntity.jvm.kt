package com.bpavuk.filery.expects

import com.bpavuk.filery.exceptions.FileDoesNotExistException
import com.bpavuk.filery.expects.pointers.DirectoryPointer
import com.bpavuk.filery.expects.pointers.FilePointer
import com.bpavuk.filery.types.Path
import java.io.File
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Paths

public actual fun fileSystemEntityBuilder(path: Path): FileSystemEntity {
    val file = File(path.path)

    return when {
        file.isDirectory -> {
            val pointer = DirectoryPointer(path)
            FileSystemEntity.Directory(path, pointer)
        }
        file.isFile -> {
            val pointer = FilePointer(path)
            FileSystemEntity.File(path, pointer)
        }
        else -> throw FileDoesNotExistException(path.path)
    }
}

public actual fun FileSystemEntity.createFile(path: Path): Boolean {
    return try {
        Files.createFile(Paths.get(path.path))
        true
    } catch (e: FileAlreadyExistsException) {
        false
    }
}

public actual fun FileSystemEntity.createDir(path: Path): Boolean {
    return try {
        Files.createDirectories(Paths.get(path.path))
        true
    } catch (e: FileAlreadyExistsException) {
        false
    }
}
