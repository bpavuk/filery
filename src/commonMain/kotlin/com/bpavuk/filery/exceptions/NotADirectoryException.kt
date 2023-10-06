package com.bpavuk.filery.exceptions

public class NotADirectoryException(private val filename: String): Exception("$filename is not a directory")
