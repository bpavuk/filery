package com.bpavuk.filery.expects

import com.bpavuk.filery.exceptions.NotDirectoryException
import com.bpavuk.filery.exceptions.NotFileException
import com.bpavuk.filery.types.Path
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readString

public class BufferedFile(
    public val path: Path,
    public val buffer: Buffer = Buffer(),
    createFileOnNonExistence: Boolean = false
) {
    public val fileSystemEntity: FileSystemEntity = fileSystemEntityBuilder(path, createFileOnNonExistence)

    private constructor(
        fileSystemEntity: FileSystemEntity,
        buffer: Buffer = Buffer()
    ) : this(fileSystemEntity.path, buffer)

    public fun createFile(path: Path): Boolean = fileSystemEntity.createFile(path)

    public fun createDir(path: Path): Boolean = fileSystemEntity.createDir(path)

    public fun close(): Boolean = fileSystemEntity.close()

    /**
     * reads bytes to [buffer]
     */
    public suspend fun readBytes(amount: Int? = null) {
        return when (fileSystemEntity) {
            is FileSystemEntity.Directory -> throw NotFileException(fileSystemEntity.path.path)
            is FileSystemEntity.File -> {
                buffer.write(fileSystemEntity.pointer.readBytes(amount))
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
                fileSystemEntity.pointer.writeBytes(readBuffer())
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
                fileSystemEntity.pointer.appendBytes(readBuffer())
            }
        }
    }

    /**
     * deletes the file and cleans the [buffer]
     * @return boolean that indicates whether deletion was successful
     */
    public fun delete(): Boolean = fileSystemEntity.delete()

    public fun openDir(path: Path): BufferedFile {
        when (val newEntity = fileSystemEntityBuilder(path)) {
            is FileSystemEntity.Directory -> return BufferedFile(newEntity, buffer)
            is FileSystemEntity.File -> throw NotDirectoryException(path.path)
        }
    }
}

public suspend fun BufferedFile.readUntil(
    includeLastByte: Boolean,
    condition: Byte.() -> Boolean
) {
    when (fileSystemEntity) {
        is FileSystemEntity.Directory -> throw NotFileException(path.path)
        is FileSystemEntity.File -> {
            val bytes = mutableListOf<Byte>()
            do {
                bytes.add(fileSystemEntity.pointer.readBytes(1)[0])
            } while (!(condition(bytes.last()) || (-1).toByte() == bytes.last()))
            buffer.write(source = bytes.apply { if (!includeLastByte) this.removeLast() }.toByteArray())
        }
    }
}
