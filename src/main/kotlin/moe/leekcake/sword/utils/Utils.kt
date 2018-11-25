package moe.leekcake.sword.utils

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

object Utils {
    fun safeClose(closeable: Closeable?) {
        if (closeable == null) return
        try {
            closeable.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun copyStream(src: InputStream, dest: OutputStream, count: Int) {
        var left = count
        var readed: Int
        val buf = ByteArray(1024)
        while (left != 0) {
            readed = src.read(buf, 0, Math.min(1024, left))
            if (readed == -1) {
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                continue
            }
            left -= readed
            dest.write(buf, 0, readed)
        }
    }

    fun cutString(value: String, start: Int, end: Int): String {
        val builder = StringBuilder()

        builder.append(value, 0, start)
        builder.append(value, end + 1, value.length)

        return builder.toString()
    }
}