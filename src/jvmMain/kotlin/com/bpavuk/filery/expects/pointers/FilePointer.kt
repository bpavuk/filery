package com.bpavuk.filery.expects.pointers

import com.bpavuk.filery.types.Modes
import com.bpavuk.filery.types.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import kotlin.math.min

public actual class FilePointer(
    override val platformFilePointer: JvmFile,
    override val mode: Modes = Modes.ReadWrite
) : IFilePointer {
    public constructor(path: Path, mode: Modes = Modes.ReadWrite): this(JvmFile(path), mode)

    override val path: Path = platformFilePointer.path

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

    override fun delete(): Boolean =
        platformFilePointer.file.delete()

}

public class JvmFile(public val path: Path) {
    public val file: File = File(path.path)
    public val stream: FileInputStream = FileInputStream(file)
}
