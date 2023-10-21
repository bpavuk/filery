package com.bpavuk.filery.expects.pointers

import com.bpavuk.filery.expects.JvmFile
import com.bpavuk.filery.types.FileType
import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

public actual class FilePointer(
    override val platformFilePointer: JvmFile,
    override val type: FileType,
    override val mode: Modes,
    override val path: Path
) : IFilePointer {
    override fun close(): Boolean {
        platformFilePointer.stream.close()
        return true
    }

    override suspend fun readBytes(amount: Int?): ByteArray {
        val available = withContext(Dispatchers.IO) {
            platformFilePointer.stream.available()
        }
        val size = if (amount != null && amount > 0) min(amount.toInt(), available) else available
        val array = ByteArray(size)

        withContext(Dispatchers.IO) {
            platformFilePointer.stream.read(array)
        }

        return array
    }

    override suspend fun writeBytes(bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            platformFilePointer.file.writeBytes(bytes)
        }
    }

    override suspend fun appendBytes(bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            platformFilePointer.file.appendBytes(bytes)
        }
    }
}