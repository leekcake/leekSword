package moe.leekcake.sword.sds

import java.io.EOFException
import java.io.IOException
import java.io.Reader
import java.io.StringReader



class SDSParser {
    private val sb_Script = StringBuilder()

    @Throws(IOException::class)
    fun parseString(string: String): SDS? {
        return parseReader(StringReader(string))
    }

    /**
     * Reader에서 텍스트 형식의 SDS를 분석합니다
     *
     * @param reader
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun parseReader(reader: Reader): SDS? {
        var result: SDS? = null;
        synchronized(sb_Script) {
            var read: Int

            var inString = false
            var inValue = false

            var ArgName: String? = null

            sb_Script.setLength(0) // Reset SB
            while (true) {
                read = reader.read();
                if (read == -1) {
                    break
                }
                val char = read.toChar()
                val c = read.toChar()
                if (sb_Script.length == 0 && char == '/') {
                    var cc = 0.toChar()
                    while (true) {
                        read = reader.read();
                        if (read == -1) {
                            break
                        }
                        cc = read.toChar()
                        if (cc == '\r' || cc == '\n') {
                            break
                        }
                    }
                    continue
                }
                if (c == ';')
                    return SDS(sb_Script.toString())
                if (c == ' ') {
                    if (sb_Script.length == 0) {
                        continue
                    } else {
                        break
                    }
                }
                if (c == '\r' || c == '\n') {
                    continue
                }
                sb_Script.append(c)
            }
            if (sb_Script.length == 0) {
                throw EOFException()
            }
            result = SDS(sb_Script.toString())

            sb_Script.setLength(0) // Reset SB
            while (true) {
                read = reader.read();
                if (read == -1) {
                    break
                }
                var c = read.toChar()

                if (c == '\\') {
                    c = reader.read().toChar()
                } else if (c == '"') {
                    if (inString) {
                        inString = false
                    } else {
                        inString = true
                    }
                    continue
                } else if (!inString) {
                    if (c == ';') {
                        break
                    } else if (c == '=' && !inValue) {
                        inValue = true
                        ArgName = sb_Script.toString()
                        sb_Script.setLength(0) // Reset SB
                        continue
                    } else if (c == ',') {
                        if (inValue) {
                            // Unpack \ ignore mark
                            result!!.putArgument(ArgName!!.trim { it <= ' ' }, sb_Script.toString().trim { it <= ' ' }.replace("\\;", ";").replace("\\,", ","))
                            inValue = false
                        } else {
                            result!!.putArgument(sb_Script.toString().trim { it <= ' ' }, null)
                        }
                        sb_Script.setLength(0) // Reset SB
                        continue
                    }
                }
                sb_Script.append(c)
            }

            if (ArgName != null && ArgName.length != 0) {
                if (inValue) {
                    // Unpack \ ignore mark
                    result!!.putArgument(ArgName.trim { it <= ' ' }, sb_Script.toString().trim { it <= ' ' }.replace("\\;", ";").replace("\\,", ","))
                    inValue = false
                } else {
                    result!!.putArgument(sb_Script.toString().trim { it <= ' ' }, null)
                }
            } else if (sb_Script.length != 0) {
                result!!.putArgument(sb_Script.toString(), null)
            }
            sb_Script.setLength(0) // Reset SB
        }
        return result
    }
}