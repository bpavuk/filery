package com.bpavuk.filery.expects

import com.bpavuk.filery.expects.pointers.DirectoryPointer
import com.bpavuk.filery.expects.pointers.FilePointer
import com.bpavuk.filery.types.Path

public sealed class FileSystemEntity(public open val path: Path) {
    public data class File(override val path: Path, val pointer: FilePointer) : FileSystemEntity(path)
    public data class Directory(override val path: Path, val pointer: DirectoryPointer) : FileSystemEntity(path)
}

public expect fun FileSystemEntity.createFile(path: Path): Boolean

public expect fun FileSystemEntity.createDir(path: Path): Boolean

public fun FileSystemEntity.delete(): Boolean = when (this) {
    is FileSystemEntity.Directory -> this.pointer.delete() // TODO
    is FileSystemEntity.File -> this.pointer.delete()
}
