package com.bpavuk.filery.exceptions

import kotlinx.io.IOException

public data class NotDirectoryException(public val filename: String): IOException("$filename is not a directory")
