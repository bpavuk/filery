package com.bpavuk.filery.exceptions

public class FileAlreadyClosedException(fileName: String) : RuntimeException("File $fileName already closed")
