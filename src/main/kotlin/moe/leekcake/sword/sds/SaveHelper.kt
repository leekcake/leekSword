package moe.leekcake.sword.sds

import java.io.DataInputStream
import java.io.DataOutputStream

object SaveHelper {
    enum class AutoType(val type: Byte) {
        Number(-128),
        String(-127),
        RawByte(-125),
        SDS(-124),
        Array(-123),
        Nothing(-122);

        companion object {
            fun from(findValue: Byte): AutoType? {
                try {
                    return AutoType.values().first { it.type == findValue }
                } catch(nse: NoSuchElementException) {
                    return null;
                }
            }
        }
    }

    enum class NumberType(val type: Byte) {
        Double(-128),
        Float(-127),
        Long(-126),
        Int(-125),
        Short(-124),
        aByte(-123);

        companion object {
            fun from(findValue: Byte): NumberType? {
                try {
                    return NumberType.values().first { it.type == findValue }
                } catch(nse: NoSuchElementException) {
                    return null;
                }
            }
        }
    }

    fun readAuto(dis: DataInputStream): Any? {
        val type: AutoType? = AutoType.from( dis.readByte() );
        if(type === null) {
            throw Exception("Bad type")
        }

        when (type) {
            AutoType.Nothing -> {
                return null;
            }
            AutoType.SDS -> {
                return SDS(dis);
            }
            AutoType.RawByte -> {
                val len = dis.readInt()
                val data = ByteArray(len)
                dis.read(data)
                return data
            }
            AutoType.String -> {
                return readString(dis);
            }
            AutoType.Number -> {
                return readNumber(dis);
            }
            AutoType.Array -> {
                return readArray(dis);
            }
        }
    }

    fun writeAuto(dos: DataOutputStream, data: Any?) {
        if(data === null) {
            dos.writeByte( AutoType.Nothing.type.toInt() );
            return;
        }

        when (data) {
            is Number -> {
                dos.writeByte( AutoType.Number.type.toInt() );
                writeNumber(dos, data);
            }
            is String -> {
                dos.writeByte( AutoType.String.type.toInt() );
                writeString(dos, data);
            }
            is ByteArray -> {
                dos.writeByte( AutoType.RawByte.type.toInt() );
                dos.writeInt( data.size );
                dos.write( data );
            }
            is Array<*> -> {
                dos.writeByte( AutoType.Array.type.toInt() )
                writeArray(dos, data as Array<Any?>);
            }
            is SDS -> {
                dos.writeByte(AutoType.SDS.type.toInt());
                data.save(dos);
            }
        }
    }

    fun readArray(dis: DataInputStream): Array<Any?> {
        val result: Array<Any?>;

        result = Array( dis.readInt(), {null} );
        for(inx: Int in 0 until result.size) {
            result[inx] = readAuto(dis);
        }

        return result;
    }

    fun writeArray(dos: DataOutputStream, data: Array<Any?>) {
        dos.writeInt( data.size );
        for(dat: Any? in data) {
            writeAuto(dos, dat);
        }
    }

    fun readNumber(dis: DataInputStream): Number {
        val code: NumberType? = NumberType.from( dis.readByte() );
        if(code === null) {
            throw Exception("Non-Numberic Data");
        }

        when(code) {
            NumberType.Double -> {
                return dis.readDouble();
            }
            NumberType.Float -> {
                return dis.readFloat();
            }
            NumberType.Int -> {
                return dis.readInt();
            }
            NumberType.Long -> {
                return dis.readLong();
            }
            NumberType.Short -> {
                return dis.readShort();
            }
            NumberType.aByte -> {
                return dis.readByte();
            }
        }
    }

    fun writeNumber(dos: DataOutputStream, number: Number) {
        when (number) {
            is Double -> {
                dos.writeByte(NumberType.Double.type.toInt());
                dos.writeDouble(number);
            }
            is Float -> {
                dos.writeByte(NumberType.Float.type.toInt());
                dos.writeFloat(number);
            }
            is Long -> {
                dos.writeByte(NumberType.Long.type.toInt());
                dos.writeLong(number);
            }
            is Int -> {
                dos.writeByte(NumberType.Int.type.toInt());
                dos.writeInt(number);
            }
            is Short -> {
                dos.writeByte(NumberType.Short.type.toInt());
                dos.writeShort(number.toInt());
            }
            is Byte -> {
                dos.writeByte(NumberType.aByte.type.toInt());
                dos.writeByte(number.toInt());
            }
        }
    }

    fun readString(dis: DataInputStream): String {
        val length = dis.readInt();
        val nameBuf = ByteArray(length)
        dis.readFully(nameBuf)
        return String( nameBuf );
    }

    fun writeString(dos: DataOutputStream, data: String) {
        val bytes: ByteArray = data.toByteArray();
        dos.writeInt( bytes.size );
        dos.write( bytes );
    }
}