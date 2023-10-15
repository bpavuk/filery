package com.bpavuk.filery.exceptions

public class NotAFileException(filename: String) : Exception("$filename is not a file")