package com.bpavuk.filery.exceptions

public class FileDoesNotExistException(fileName: String) : RuntimeException("file $fileName does not exist")
