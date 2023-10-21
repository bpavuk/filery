package com.bpavuk.filery.exceptions

import kotlinx.io.IOException

public data class FileDoesNotExistException(private val fileName: String) : IOException("file $fileName does not exist")
