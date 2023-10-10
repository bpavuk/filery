package com.bpavuk.filery.exceptions

public class FileNotOpenException(fileName: String) : RuntimeException("file $fileName is not open")
