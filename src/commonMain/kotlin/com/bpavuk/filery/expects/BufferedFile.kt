package com.bpavuk.filery.expects

import com.bpavuk.filery.exceptions.NotFileException
import com.bpavuk.filery.types.Path
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readString

class BufferedFile(
    public val path: Path,
    public val buffer: Buffer = Buffer()
) {
    public lateinit var fileSystemEntity: FileSystemEntity
        private set

    public fun createFile(path: Path): Boolean = fileSystemEntity.createFile(path)

    public fun createDir(path: Path): Boolean = fileSystemEntity.createDir(path)

    public fun close(): Boolean = when (fileSystemEntity) {
        is FileSystemEntity.File -> (fileSystemEntity as FileSystemEntity.File).pointer.close()
        is FileSystemEntity.Directory -> true
    }

    /**
     * reads bytes to [buffer]
     */
    public suspend fun readBytes(amount: Int? = null) {
        return when (fileSystemEntity) {
            is FileSystemEntity.Directory -> throw NotFileException(fileSystemEntity.path.path)
            is FileSystemEntity.File -> {
                buffer.write((fileSystemEntity as FileSystemEntity.File).pointer.readBytes(amount))
            }
        }
    }

    /**
     * reads the whole buffer and returns its contents
     */
    public fun readBuffer(): ByteArray = buffer.readByteArray()

    /**
     * same as [readBuffer] but returns content as string
     */
    public fun readBufferAsString(): String = buffer.readString()

    /**
     * writes directly from [buffer] to file, overwriting it
     */
    public suspend fun writeToFile() {
        return when (fileSystemEntity) {
            is FileSystemEntity.Directory -> throw NotFileException(path.path)
            is FileSystemEntity.File -> {
                (fileSystemEntity as FileSystemEntity.File).pointer.writeBytes(readBuffer())
            }
        }
    }

    /**
     * appends bytes from [buffer] to file instead of overwriting it
     */
    public suspend fun appendToFile() {
        return when (fileSystemEntity) {
            is FileSystemEntity.Directory -> throw NotFileException(path.path)
            is FileSystemEntity.File -> {
                (fileSystemEntity as FileSystemEntity.File).pointer.appendBytes(readBuffer())
            }
        }
    }

    /**
     * deletes the file and cleans the [buffer]
     * @return boolean that indicates whether deletion was successful
     */
    public fun delete(): Boolean = fileSystemEntity.delete()
}
