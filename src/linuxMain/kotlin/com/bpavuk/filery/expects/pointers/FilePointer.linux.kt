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
    private val modeAsString: String = when (mode) {
        Modes.Read -> "r"
        Modes.Write -> "r+"
        Modes.ReadWrite -> "r+"
    }
    override val platformFilePointer: CFilePointer = CFilePointer(
        currentPointer = fopen(path.path, modeAsString)
            ?: throw FileNotFoundException(path.path)
    )

    override fun close(): Boolean =
        fclose(platformFilePointer.currentPointer) == 0

    override fun delete(): Boolean = remove(path.path) == 0

    override suspend fun readBytes(amount: Int?): ByteArray {
        platformFilePointer.currentPosition = ftell(platformFilePointer.currentPointer)
        platformFilePointer.currentPointer = fopen(path.path, "r") ?: throw FileNotFoundException(path.path)
        fseek(platformFilePointer.currentPointer, 0, SEEK_END)
        val fileSize = ftell(platformFilePointer.currentPointer)
        val bytesLeft = (fileSize - platformFilePointer.currentPosition).toULong()
        fseek(platformFilePointer.currentPointer, platformFilePointer.currentPosition, SEEK_SET)

        val realAmount = if (amount == null || amount < 0) bytesLeft.toInt() else amount
        println(realAmount)
        return if (realAmount < 0) {
            ByteArray(1) { -1 }
        } else {
            val cByteArray = nativeHeap.allocArray<ByteVar>(realAmount)
            fread(cByteArray, 1.toULong(), realAmount.toULong(), platformFilePointer.currentPointer)
            val result = cByteArray.readBytes(realAmount)
            nativeHeap.free(cByteArray)
            result
        }
    }

    override suspend fun writeBytes(bytes: ByteArray) {
        TODO("Not yet implemented")
    }

    override suspend fun appendBytes(bytes: ByteArray) {
        TODO("Not yet implemented")
    }
}

@OptIn(ExperimentalForeignApi::class)
public data class CFilePointer(
    var currentPointer: CPointer<FILE>,
    var currentPosition: Long = 0
)
