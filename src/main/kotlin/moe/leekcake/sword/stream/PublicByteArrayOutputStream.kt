package moe.leekcake.sword.stream

import java.io.ByteArrayOutputStream
import java.util.*

class PublicByteArrayOutputStream(count: Int) : ByteArrayOutputStream(count) {
    constructor() : this(32);

    fun getByteArrayAvoidCopy(): ByteArray {
        if(count != buf.size) {
            buf = Arrays.copyOf(buf, count);
        }

        return buf;
    }

    fun getCount(): Int {
        return count;
    }
}