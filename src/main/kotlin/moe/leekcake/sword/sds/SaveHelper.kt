package moe.leekcake.sword.sds

import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object SaveHelper {
    enum class AutoType(val type: Byte) {
        Number(-128),
        String(-127),
        KotlinObject(-126),
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

    fun readAuto(ois: ObjectInputStream): Any? {
        val type: AutoType? = AutoType.from( ois.readByte() );
        if(type === null) {
            throw Exception("Bad type")
        }

        when (type) {
            AutoType.Nothing -> {
                return null;
            }
            AutoType.SDS -> {
                return SDS(ois);
            }
            AutoType.RawByte -> {
                val len = ois.readInt()
                val data = ByteArray(len)
                ois.read(data)
                return data
            }
            AutoType.String -> {
                return readString(ois);
            }
            AutoType.Number -> {
                return readNumber(ois);
            }
            AutoType.Array -> {
                return readArray(ois);
            }
            AutoType.KotlinObject -> {
                return ois.readObject();
            }
        }
    }

    fun writeAuto(oos: ObjectOutputStream, data: Any?) {
        if(data === null) {
            oos.writeByte( AutoType.Nothing.type.toInt() );
            return;
        }

        when (data) {
            is Number -> {
                oos.writeByte( AutoType.Number.type.toInt() );
                writeNumber(oos, data);
            }
            is String -> {
                oos.writeByte( AutoType.String.type.toInt() );
                writeString(oos, data);
            }
            is ByteArray -> {
                oos.writeByte( AutoType.RawByte.type.toInt() );
                oos.writeInt( data.size );
                oos.write( data );
            }
            is Array<*> -> {
                writeArray(oos, data as Array<Any?>);
            }
            is SDS -> {
                oos.writeByte(AutoType.SDS.type.toInt());
                data.save(oos);
            }
            else -> {
                oos.writeByte( AutoType.KotlinObject.type.toInt() );
                oos.writeObject(data);
            }
        }
    }

    fun readArray(ois: ObjectInputStream): Array<Any?> {
        val result: Array<Any?>;

        result = Array( ois.readInt(), {null} );
        for(inx: Int in 0 until result.size) {
            result[inx] = readAuto(ois);
        }

        return result;
    }

    fun writeArray(oos: ObjectOutputStream, data: Array<Any?>) {
        oos.writeInt( data.size );
        for(dat: Any? in data) {
            writeAuto(oos, dat);
        }
    }

    fun readNumber(ois: ObjectInputStream): Number {
        val code: NumberType? = NumberType.from( ois.readByte() );
        if(code === null) {
            throw Exception("Non-Numberic Data");
        }

        when(code) {
            NumberType.Double -> {
                return ois.readDouble();
            }
            NumberType.Float -> {
                return ois.readFloat();
            }
            NumberType.Int -> {
                return ois.readInt();
            }
            NumberType.Long -> {
                return ois.readLong();
            }
            NumberType.Short -> {
                return ois.readShort();
            }
            NumberType.aByte -> {
                return ois.readByte();
            }
        }
    }

    fun writeNumber(oos: ObjectOutputStream, number: Number) {
        when (number) {
            is Double -> {
                oos.writeByte(NumberType.Double.type.toInt());
                oos.writeDouble(number);
            }
            is Float -> {
                oos.writeByte(NumberType.Float.type.toInt());
                oos.writeFloat(number);
            }
            is Long -> {
                oos.writeByte(NumberType.Long.type.toInt());
                oos.writeLong(number);
            }
            is Int -> {
                oos.writeByte(NumberType.Int.type.toInt());
                oos.writeInt(number);
            }
            is Short -> {
                oos.writeByte(NumberType.Short.type.toInt());
                oos.writeShort(number.toInt());
            }
            is Byte -> {
                oos.writeByte(NumberType.aByte.type.toInt());
                oos.writeByte(number.toInt());
            }
        }
    }

    fun readString(ois: ObjectInputStream): String {
        val length = ois.readInt();
        val nameBuf = ByteArray(length)
        ois.readFully(nameBuf)
        return String( nameBuf );
    }

    fun writeString(oos: ObjectOutputStream, data: String) {
        val bytes: ByteArray = data.toByteArray();
        oos.writeInt( bytes.size );
        oos.write( bytes );
    }
}