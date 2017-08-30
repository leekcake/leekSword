package moe.leekcake.sword

import java.text.SimpleDateFormat
import java.util.*


open class Logger(val level: Level = Level.INFO, dateFormat: String = "yyyy-MM-dd HH:mm:ss.SSS") {
    companion object {
        internal val logger = Logger(Level.DEBUG)
    }

    val dateFormat = SimpleDateFormat(dateFormat)

    enum class Level(val level: Int) {
        FATAL(0),
        ERROR(1),
        WARNING(2),
        INFO(3),
        VERBOSE(4),
        DEBUG(5)
    }
    fun levelToKorean(level: Level): String {
        return when(level) {
            Level.FATAL -> "심각"
            Level.ERROR -> "오류"
            Level.WARNING -> "경고"
            Level.INFO -> "정보"
            Level.VERBOSE -> "상세"
            Level.DEBUG -> "디버그"
        }
    }

    fun fatal(message: String, tag: String = "") {
        print(Level.FATAL, message, tag);
    }

    fun error(message: String, tag: String = "") {
        print(Level.ERROR, message, tag);
    }

    fun warning(message: String, tag: String = "") {
        print(Level.WARNING, message, tag);
    }

    fun info(message: String, tag: String = "") {
        print(Level.INFO, message, tag);
    }

    fun verbose(message: String, tag: String = "") {
        print(Level.VERBOSE, message, tag);
    }

    fun debug(message: String, tag: String = "") {
        print(Level.DEBUG, message, tag);
    }

    fun print(level: Level, message: String, tag: String = "") {
        if(level > this.level) {
            return
        }

        val value: String;

        if(tag == "") {
            value = ( "${dateFormat.format(Date())} [${levelToKorean(level)}] $message" );
        } else {
            value = ( "${dateFormat.format(Date())} [${levelToKorean(level)}] [$tag] $message" );
        }

        if(level < Level.INFO) {
            printError(value)
        } else {
            print(value)
        }
    }

    open protected fun print(value: String) {
        System.out.println(value)
    }

    open protected fun printError(value: String) {
        System.err.println(value)
    }
}