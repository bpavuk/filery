package com.bpavuk.filery.expects

import com.bpavuk.filery.types.Path
import java.nio.file.Files
import java.nio.file.Paths

public actual object Utils : IUtils {
    override fun exists(path: Path): Boolean = Files.exists(Paths.get(path.path))
}