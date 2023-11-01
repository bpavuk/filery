package com.bpavuk.filery.types

public data class Path(public val path: String) {
    public operator fun plus(otherPath: Path): Path {
        return Path(path + otherPath)
    }
}
