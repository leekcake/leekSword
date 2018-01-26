package moe.leekcake.sword.sds

import moe.leekcake.sword.Logger

import moe.leekcake.sword.Logger.Companion.logger;
import java.io.*

/**
 * Simple(Stupid) Data Structure
 *
 * JSON, BSON 같이 데이터를 저장하고 불러오는데 쓰이는 클래스
 *
 * 이전 데이터 방식과의 호환성을 위해 사용
 */
class SDS {
    val name: String;
    val arguments: HashMap<String, Any?> = HashMap();

    constructor(name: String, vararg arguments: Any?) {
        this.name = name;
        for(inx in 0 until arguments.size step 2) {
            if(arguments[inx] !is String) {
                throw Exception("Invalid argument, require like [string, any, string, any]");
            }
            this.arguments[ arguments[inx] as String ] = arguments[inx+1];
        }
    }

    constructor(data: ByteArray) : this( ByteArrayInputStream(data) )

    constructor(input: InputStream) {
        val dis = DataInputStream(input)

        name = SaveHelper.readString(dis);

        val count: Int = dis.readInt();
        for(inx in 0 until count) {
            val nameOnly: Boolean = dis.readBoolean();
            val name: String = SaveHelper.readString(dis);

            if(nameOnly) {
                putArgument(name, null);
            } else {
                putArgument(name, SaveHelper.readAuto(dis));
            }
        }
    }

    fun save(): ByteArray {
        val baos = ByteArrayOutputStream();
        save(baos);
        return baos.toByteArray();
    }

    fun save(output: OutputStream) {
        val dos = DataOutputStream(output);

        SaveHelper.writeString(dos, name);

        dos.writeInt( arguments.size );

        for((key, value) in arguments) {
            dos.writeBoolean( value === null );
            SaveHelper.writeString(dos, key);
            if(value !== null) {
                SaveHelper.writeAuto(dos, value);
            }
        }

        output.flush();
    }

    fun dump(level: Logger.Level) {
        logger.print(level, toString());
    }

    fun dumpDetail(level: Logger.Level) {
        logger.print(level, "Name: $name");
        for((key, value) in arguments) {
            if(value != null) {
                logger.print(level, "argument '$key' = '$value'");
            } else {
                logger.print(level, "argument '$key' = nothing");
            }
        }
    }

    fun containsArgument(name: String): Boolean {
        return arguments.containsKey(name);
    }

    fun getArgument(name: String): Any? {
        return arguments[name];
    }

    inline fun<reified T> getArgumentArray(name: String): Array<T>? {
        val argument: Any? = arguments[name];

        if(argument is Array<*>) {
            return Array(argument.size) {
                i -> argument[i] as T
            }
        }

        return null
    }

    fun getArgumentString(name: String): String? {
        val argument: Any? = arguments[name];
        if(argument === null) {
            return null;
        }

        if( argument is String ) {
            return argument;
        } else {
            return argument.toString();
        }
    }

    fun getArgumentStringArray(name: String): Array<String>? {
        val argument: Any? = arguments[name];

        if(argument is Array<*>) {
            return Array(argument.size) {
                i -> argument[i] as String
            };
        }

        return null;
    }

    fun getArgumentNumber(name: String): Number {
        val argument: Any? = arguments[name];

        if( argument === null ) {
            throw Exception("$name is Empty field");
        }

        if( argument is String ) {
            return argument.toDouble();
        }

        if(argument !is Number) {
            throw Exception("$name is not Number");
        }

        return argument;
    }

    fun putArgument(name: String, data: Any?) {
        if( arguments.containsKey(name) ) {
            arguments.remove(name);
        }
        arguments[name] = data;
    }

    override fun toString(): String {
        val builder = StringBuilder();
        builder.append(name);
        builder.append(" {");
        for((key, value) in arguments) {
            builder.append(key);
            if(value != null) {
                builder.append("=");
                builder.append( value.toString() );
            }
            builder.append(',');
        }
        //Cut last ','
        builder.setLength( builder.length - 1 );
        builder.append('}');

        return builder.toString();
    }
}