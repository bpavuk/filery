package com.bpavuk.filery.expects

import com.bpavuk.filery.types.Path

public interface IUtils {
    public fun exists(path: Path): Boolean
}

public expect object Utils : IUtils