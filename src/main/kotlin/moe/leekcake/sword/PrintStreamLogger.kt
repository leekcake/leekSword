package moe.leekcake.sword

import java.io.PrintStream

class PrintStreamLogger(val ps: PrintStream): Logger() {
    override fun print(value: String) {
        ps.println(value)
    }

    override fun printError(value: String) {
        ps.println(value)
    }
}