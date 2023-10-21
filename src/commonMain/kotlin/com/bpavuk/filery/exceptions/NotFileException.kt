package com.bpavuk.filery.exceptions

import kotlinx.io.IOException

public data class NotFileException(val filename: String) : IOException("$filename is not a file")
