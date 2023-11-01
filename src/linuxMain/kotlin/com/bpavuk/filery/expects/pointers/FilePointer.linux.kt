package com.bpavuk.filery.expects.pointers

import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
import kotlinx.cinterop.*
import kotlinx.io.files.FileNotFoundException
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
public actual class FilePointer actual constructor(
    override val path: Path,
    override val mode: Modes
) : IFilePointer {
    override val platformFilePointer: CFilePointer = CFilePointer(
        readPointer = fopen(path.path, "r")
            ?: throw FileNotFoundException(path.path),
        writePointer = lazy { fopen(path.path, "w") ?: throw FileNotFoundException(path.path) },
        appendPointer = fopen(path.path, "a") ?: throw FileNotFoundException(path.path)
    )

    override fun close(): Boolean =
        fclose(platformFilePointer.readPointer) == 0

    override fun delete(): Boolean = remove(path.path) == 0

    override suspend fun readBytes(amount: Int?): ByteArray {
        platformFilePointer.currentPosition = ftell(platformFilePointer.readPointer)
        fseek(platformFilePointer.readPointer, 0, SEEK_END)
        val fileSize = ftell(platformFilePointer.readPointer)
        val bytesLeft = (fileSize - platformFilePointer.currentPosition).toULong()
        fseek(platformFilePointer.readPointer, platformFilePointer.currentPosition, SEEK_SET)

        val realAmount = if (amount == null || amount < 0) bytesLeft.toInt() else amount
        return if (realAmount < 0) {
            ByteArray(1) { -1 }
        } else {
            val cByteArray = nativeHeap.allocArray<ByteVar>(realAmount)
            fread(cByteArray, 1.toULong(), realAmount.toULong(), platformFilePointer.readPointer)
            val result = cByteArray.readBytes(realAmount)
            nativeHeap.free(cByteArray)
            result
        }
    }

    override suspend fun writeBytes(bytes: ByteArray) {
        val cByteArray = nativeHeap.allocArrayOf(bytes)
        fseek(platformFilePointer.writePointer.value, platformFilePointer.currentPosition, SEEK_SET)
        fwrite(cByteArray, bytes.size.toULong(), 1u, platformFilePointer.writePointer.value)
        fclose(platformFilePointer.writePointer.value)
        platformFilePointer.writePointer = lazy { fopen(path.path, "w") ?: throw FileNotFoundException(path.path) }
    }

    override suspend fun appendBytes(bytes: ByteArray) {
        val cByteArray = nativeHeap.allocArrayOf(bytes)
        fseek(platformFilePointer.appendPointer, 0, SEEK_END)
        fwrite(cByteArray, bytes.size.toULong(), 1u, platformFilePointer.appendPointer)
        fclose(platformFilePointer.appendPointer)
        platformFilePointer.appendPointer = fopen(path.path, "a") ?: throw FileNotFoundException(path.path)
    }
}

@OptIn(ExperimentalForeignApi::class)
public data class CFilePointer(
    var readPointer: CPointer<FILE>,
    var writePointer: Lazy<CPointer<FILE>>,
    var appendPointer: CPointer<FILE>?,
    var currentPosition: Long = 0
)
