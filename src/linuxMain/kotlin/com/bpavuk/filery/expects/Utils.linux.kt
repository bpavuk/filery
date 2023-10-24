package com.bpavuk.filery.expects

import com.bpavuk.filery.types.Path
import platform.posix.F_OK
import platform.posix.access

public actual object Utils : IUtils {
    override fun exists(path: Path): Boolean = access(path.path, F_OK) != -1
}