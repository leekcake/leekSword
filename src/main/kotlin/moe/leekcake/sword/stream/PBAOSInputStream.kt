package moe.leekcake.sword.stream

import java.io.InputStream

class BAOSInputStream(private val baos: PublicByteArrayOutputStream) : InputStream() {
    private var inx: Int = 0;

    override fun skip(n: Long): Long {
        var len: Int = n.toInt();
        if(len + inx > baos.getCount()) {
            len = baos.getCount() - (inx - 1);
        }
        inx += len;

        return len.toLong();
    }

    override fun available(): Int {
        return (baos.getCount()) - inx;
    }

    override fun markSupported(): Boolean {
        return false;
    }

    override fun read(): Int {
        if(inx >= baos.getCount())
            return -1;

        return baos.getByteArrayAvoidCopy()[inx++].toInt();
    }

    override fun read(b: ByteArray?, off: Int, n: Int): Int {
        var len: Int = n;
        if(inx >= baos.getCount())
            return -1;

        if(len + inx > baos.getCount()) {
            len = baos.getCount() - (inx + 1);
        }

        if(len == -1) {
            return -1;
        }

        System.arraycopy(baos.getByteArrayAvoidCopy(), inx, b, off, len);
        inx += len;
        return len;
    }
}