package io.github.alexzhirkevich.compottie

import okio.FileHandle
import okio.FileMetadata
import okio.FileSystem
import okio.Path
import okio.Sink
import okio.Source

@InternalCompottieApi
public actual fun defaultFileSystem() : FileSystem = ThrowingFileSystem

private object ThrowingFileSystem : FileSystem() {

    override fun atomicMove(source: Path, target: Path) {
        throwReadWriteIsUnsupported()
    }

    override fun canonicalize(path: Path): Path {
        throwReadWriteIsUnsupported()
    }

    override fun createDirectory(dir: Path, mustCreate: Boolean) {
        throwReadWriteIsUnsupported()
    }

    override fun createSymlink(source: Path, target: Path) {
        throwReadWriteIsUnsupported()
    }

    override fun delete(path: Path, mustExist: Boolean) {
        throwReadWriteIsUnsupported()
    }

    override fun list(dir: Path): List<Path> {
        throwReadWriteIsUnsupported()
    }

    override fun listOrNull(dir: Path): List<Path>? {
        throwReadWriteIsUnsupported()
    }

    override fun metadataOrNull(path: Path): FileMetadata? {
        throwReadWriteIsUnsupported()
    }

    override fun openReadOnly(file: Path): FileHandle {
        throwReadWriteIsUnsupported()
    }

    override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
        throwReadWriteIsUnsupported()
    }

    override fun sink(file: Path, mustCreate: Boolean): Sink {
        throwReadWriteIsUnsupported()
    }

    override fun appendingSink(file: Path, mustExist: Boolean): Sink {
        throwReadWriteIsUnsupported()
    }

    override fun source(file: Path): Source {
        throwReadWriteIsUnsupported()
    }

    @OptIn(InternalCompottieApi::class)
    private fun throwReadWriteIsUnsupported(): Nothing {
        throw UnsupportedFileSystemException()
    }
}

