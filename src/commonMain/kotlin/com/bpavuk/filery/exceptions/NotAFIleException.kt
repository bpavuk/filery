package com.bpavuk.filery.exceptions

public class NotAFIleException(filename: String) : Exception("$filename is not a file")