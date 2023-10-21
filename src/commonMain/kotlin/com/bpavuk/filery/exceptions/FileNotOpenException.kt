package com.bpavuk.filery.exceptions

import kotlinx.io.IOException

public data class FileNotOpenException(val fileName: String) : IOException("file $fileName is not open")
