package com.enteld.core.output

import com.enteld.core.token.Token
import com.enteld.paramsreader.Params
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

interface DumpPrint {
    val errors: MutableMap<Token, String>
    val warnings: MutableMap<Token, String>

    companion object {
        private const val DUMP_DIR = "dumps"
        private val date = SimpleDateFormat("dd_mm_yyyy_hh_mm_ss").format(Calendar.getInstance().time)
        val DUMP_FOLDER = "dumps/$date"
    }

    fun printDump(params: List<Params>, path: String)

    fun printDumpToFile(params: List<Params>, path: String) {
        val dir = File(DUMP_DIR)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val dirDate = File(DUMP_FOLDER)
        if (!dirDate.exists()) {
            dirDate.mkdir()
        }
    }

    fun printErrors(path: String): Boolean {
        errors.forEach { (token, message) ->
            with(OutputTerm()) {
                println(red("E: $message\n${token.dumpTab(path)}"))
            }
        }
        return errors.isNotEmpty()
    }

    fun printWarnings(path: String): Boolean {
        warnings.forEach { (token, message) ->
            with(OutputTerm()) {
                println(yellow("W: $message\n${token.dumpTab(path)}"))
            }
        }
        return warnings.isNotEmpty()
    }

}