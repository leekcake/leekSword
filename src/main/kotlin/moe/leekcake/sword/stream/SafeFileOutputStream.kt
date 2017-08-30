package moe.leekcake.sword.stream

import moe.leekcake.sword.utils.Utils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class SafeFileOutputStream(private val destFile: File) : FileOutputStream(File(destFile.parentFile, destFile.name + ".tmp")) {
    private val saveingFile: File
    private val bakFile: File

    init {
        saveingFile = File(destFile.parentFile, destFile.name + ".tmp")
        bakFile = File(destFile.parentFile, destFile.name + ".bak")

        if (bakFile.exists()) {
            bakFile.delete()
        }
    }

    fun safeRename(src: File, dest: File) {
        if (dest.exists()) {
            throw IOException()
        }

        if (src.renameTo(dest)) {
            return
        }

        if (!src.exists() && dest.exists()) return
        val fis = FileInputStream(src)
        val fos = FileOutputStream(dest)
        Utils.copyStream(fis, fos, src.length().toInt())
        fis.close()
        fos.close()
        src.delete()
    }

    override fun close() {
        super.close()
        if (!saveingFile.exists()) {
            return
        }

        if (destFile.exists()) {
            safeRename(destFile, bakFile)
        }
        safeRename(saveingFile, destFile)
        bakFile.delete()
    }
}