package moe.leekcake.sword.stream

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

class SafeFileInputStream(file: File) : InputStream() {
    val fis: FileInputStream

    init {
        if (file.exists()) {
            fis = FileInputStream(file)
        } else {
            val bak = File(file.parentFile, file.name + ".bak")
            if (bak.exists()) {
                fis = FileInputStream(bak)
            } else {
                throw FileNotFoundException()
            }
        }
    }

    override fun read(): Int {
        return fis.read()
    }

    override fun read(bytes: ByteArray): Int {
        return fis.read(bytes)
    }

    override fun read(bytes: ByteArray, i: Int, i1: Int): Int {
        return fis.read(bytes, i, i1)
    }

    override fun skip(l: Long): Long {
        return fis.skip(l)
    }

    override fun available(): Int {
        return fis.available()
    }

    override fun close() {
        fis.close()
    }

    @Synchronized override fun mark(i: Int) {
        fis.mark(i)
    }

    @Synchronized
    override fun reset() {
        fis.reset()
    }

    override fun markSupported(): Boolean {
        return fis.markSupported()
    }
}