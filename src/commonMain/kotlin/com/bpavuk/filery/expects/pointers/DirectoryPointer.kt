package com.bpavuk.filery.expects.pointers

import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path

public interface IDirectoryPointer {
    public val path: Path
    public val platformDirectoryPointer: Any
    public val mode: Modes

    public suspend fun listFiles(): List<Path>

    public fun go(relativePath: String, mode: Modes = this.mode): IDirectoryPointer

    public fun create(relativePath: String): IDirectoryPointer

    public fun delete(): Boolean
}

public expect class DirectoryPointer(
    path: Path,
    mode: Modes = Modes.ReadWrite
) : IDirectoryPointer
