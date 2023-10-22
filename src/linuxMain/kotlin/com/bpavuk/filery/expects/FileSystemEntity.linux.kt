package com.bpavuk.filery.expects

import com.bpavuk.filery.types.Path
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.F_OK
import platform.posix.access
import platform.posix.fclose
import platform.posix.fopen

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

}