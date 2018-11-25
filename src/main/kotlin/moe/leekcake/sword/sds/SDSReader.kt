package moe.leekcake.sword.sds

import java.io.InputStream

class SDSReader(val input: InputStream) {
    fun close() {
        input.close()
    }

    fun next(): SDS {
        return SDS(input)
    }
}