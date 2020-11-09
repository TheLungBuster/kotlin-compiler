package com.enteld.core.output

import com.enteld.paramsreader.Params

interface DumpPrint {
    fun printDump(params: List<Params>, path: String)
}