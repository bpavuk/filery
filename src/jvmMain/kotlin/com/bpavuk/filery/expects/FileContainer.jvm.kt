package com.bpavuk.filery.expects

import com.bpavuk.filery.exceptions.FileAlreadyClosedException
import com.bpavuk.filery.exceptions.FileDoesNotExistException
import com.bpavuk.filery.exceptions.FileNotOpenException
import com.bpavuk.filery.types.FileType
import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import java.io.File
import java.io.FileInputStream

internal actual class FileContainerImpl actual constructor(
    private val path: Path,
    override val buffer: Buffer
) : FileContainer {
    override var file: FileryFile? = null
    override var type: FileType? = null
    override var mode: Modes? = null
    private var fileInputStream: FileInputStream? = null

    override suspend fun open(mode: Modes) {
        val file = File(path.path)
        if (file.exists()) {
            this.file = file
            this.mode = mode
            if ((this.file as File).isDirectory) {
                this.type = FileType.DIRECTORY
            } else if ((this.file as File).isFile) {
                this.fileInputStream = (file as File?)?.inputStream()
                this.type = FileType.FILE
            }
        } else {
            throw FileDoesNotExistException(path.path)
        }
    }

    override suspend fun create() {
        withContext(Dispatchers.IO) {
            File(path.path).createNewFile()
        }
    }

    override suspend fun exists(): Boolean = File(path.path).exists()

    override suspend fun close() {
        fileInputStream = null
        super.close()
    }

    override suspend fun readBytes(amount: Int) = withContext(Dispatchers.IO) {
        val realSize = if (amount > -1) {
            amount
        } else {
            fileInputStream?.available()
                ?: throw FileNotOpenException(fileName = path.path)
        }
        val array = ByteArray(realSize)
        fileInputStream?.read(array) ?: throw FileAlreadyClosedException(path.path)
        buffer.write(source = array)
    }

    private suspend fun readByteImmediately(): Byte {
        val array = ByteArray(1)
        return withContext(Dispatchers.IO) {
            if (
                (fileInputStream?.read(array)
                    ?: throw FileAlreadyClosedException(path.path)) == -1
            ) {
                -1
            } else {
                array[0]
            }
        }
    }

    override suspend fun readUntil(
        includeLastByte: Boolean,
        condition: Byte.() -> Boolean
    ) {
        val bytes = mutableListOf<Byte>()
        do {
            bytes.add(readByteImmediately())
        } while (!(condition(bytes.last()) || (-1).toByte() == bytes.last()))
        buffer.write(source = bytes.apply { if (!includeLastByte) this.removeLast() }.toByteArray())
    }

    override suspend fun writeBytes(bytes: ByteArray) {
        buffer.write(bytes)
    }

    override suspend fun writeToFile() = withContext(Dispatchers.IO) {
        (file as File?)?.writeBytes(readBuffer())
            ?: throw FileNotOpenException(path.path)
    }

    override suspend fun appendToFile() = withContext(Dispatchers.IO) {
        (file as File?)?.appendBytes(buffer.readByteArray())
            ?: throw FileNotOpenException(path.path)
    }

    override suspend fun isDirectory(): Boolean = (file as File?)?.isDirectory == true
    override suspend fun isRegularFile(): Boolean = (file as File?)?.isFile == true
}
